package io.oodesigns.modbus.core;

import java.util.Optional;
import java.util.function.Function;

/**
 * The result of any operation in this library.
 *
 * <p>Nothing in this codebase throws outside a constructor: every operation answers with a
 * {@code Response} that is either a {@link Success} carrying a value, or a {@link Failure}
 * carrying the details of what went wrong. Combinators ({@link #map}, {@link #flatMap},
 * {@link #fold}) let callers compose operations without ever handling an exception: a failure
 * cascades through a chain unchanged, and any exception thrown by a caller-supplied function is
 * converted into a failure so that no exception escapes a {@code Response} boundary.</p>
 *
 * @param <T> type of the value carried by a successful response
 */
public sealed interface Response<T> permits Response.Success, Response.Failure {

    /**
     * Creates a successful response carrying a value.
     *
     * @param value the value; must not be {@code null}
     * @return a success response
     * @throws IllegalArgumentException if {@code value} is {@code null}
     */
    static <T> Response<T> success(T value) {
        if (value == null) {
            throw new IllegalArgumentException("a successful value is required; use success() for void results");
        }
        return new Success<>(value);
    }

    /**
     * Creates a successful response for an operation that produces no value.
     *
     * @return a success response with an empty value
     */
    static Response<Void> success() {
        return new Success<>(null);
    }

    /**
     * Creates a failure response.
     *
     * @param details human readable explanation; must not be {@code null} or blank
     * @return a failure response
     * @throws IllegalArgumentException if {@code details} is {@code null} or blank
     */
    static <T> Response<T> failure(String details) {
        return new Failure<>(details);
    }

    /**
     * Creates a failure response describing a caught exception. Used at the boundaries where
     * third-party code (or the JDK) still communicates by throwing.
     *
     * @param context what was being attempted; must not be {@code null} or blank
     * @param cause the exception that was caught; must not be {@code null}
     * @return a failure response combining context and the exception message
     * @throws IllegalArgumentException if {@code context} is blank or {@code cause} is {@code null}
     */
    static <T> Response<T> failure(String context, Throwable cause) {
        if (cause == null) {
            throw new IllegalArgumentException("cause is required");
        }
        return new Failure<>(context + ": " + cause);
    }

    /** @return {@link Status#OK} for a success, {@link Status#EXCEPTION} for a failure */
    Status status();

    /** @return the failure details, or an empty string for a success; never {@code null} */
    String details();

    /** @return the value of a success, or empty for a failure or a void success; never {@code null} */
    Optional<T> value();

    /** @return {@code true} when this response is a success */
    default boolean isSuccess() {
        return status() == Status.OK;
    }

    /** @return {@code true} when this response is a failure */
    default boolean isFailure() {
        return !isSuccess();
    }

    /**
     * Returns the value of a success, or the supplied fallback when there is none.
     *
     * @param fallback value to use when this response carries no value
     * @return the carried value or the fallback
     */
    default T orElse(T fallback) {
        return value().orElse(fallback);
    }

    /**
     * Re-types a failure so it can cascade into a differently typed chain.
     *
     * @return this failure, re-typed
     * @throws IllegalStateException never — a success simply cannot be re-typed and answers a
     *     failure describing the misuse
     */
    @SuppressWarnings("unchecked")
    default <U> Response<U> asFailure() {
        return isFailure()
                ? (Response<U>) this
                : Response.failure("Cannot re-type a successful response as a failure");
    }

    /**
     * Transforms the value of a success; a failure cascades unchanged.
     *
     * <p>Postcondition: the returned response is a failure whenever this response is a failure,
     * with identical details. If {@code mapper} throws, the exception is captured as a failure.</p>
     *
     * @param mapper transformation to apply to the carried value
     * @return the transformed response, never {@code null}
     */
    default <U> Response<U> map(Function<? super T, ? extends U> mapper) {
        return flatMap(value -> Response.success(mapper.apply(value)));
    }

    /**
     * Composes this response with an operation that itself answers a response.
     *
     * <p>Postcondition: a failure cascades unchanged; a success with no value becomes a failure,
     * because there is nothing to compose with. If {@code mapper} throws, the exception is
     * captured as a failure.</p>
     *
     * @param mapper operation to apply to the carried value
     * @return the composed response, never {@code null}
     */
    @SuppressWarnings("unchecked")
    default <U> Response<U> flatMap(Function<? super T, ? extends Response<U>> mapper) {
        if (isFailure()) {
            return (Response<U>) this;
        }
        if (mapper == null) {
            return Response.failure("No transformation supplied");
        }
        try {
            Optional<T> value = value();
            if (value.isEmpty()) {
                return Response.failure("No value to transform");
            }
            Response<U> result = mapper.apply(value.get());
            return result == null ? Response.failure("Transformation produced no response") : result;
        } catch (RuntimeException e) {
            return Response.failure("Transformation failed", e);
        }
    }

    /**
     * Collapses this response into a single value by applying the branch matching its status.
     *
     * @param onSuccess applied to the carried value of a success
     * @param onFailure applied to the details of a failure
     * @return the value produced by the selected branch
     */
    default <U> U fold(Function<? super T, ? extends U> onSuccess, Function<String, ? extends U> onFailure) {
        return isSuccess() && value().isPresent()
                ? onSuccess.apply(value().get())
                : onFailure.apply(details());
    }

    /**
     * A successful response.
     *
     * @param <T> type of the carried value
     */
    record Success<T>(T carried) implements Response<T> {

        /**
         * @param carried the value, or {@code null} only for a {@code Response<Void>} created
         *     through {@link Response#success()}
         */
        public Success {
            // A null value is reserved for void successes created through Response.success().
        }

        @Override
        public Status status() {
            return Status.OK;
        }

        @Override
        public String details() {
            return "";
        }

        @Override
        public Optional<T> value() {
            return Optional.ofNullable(carried);
        }
    }

    /**
     * A failed response.
     *
     * @param <T> type the response would have carried had it succeeded
     */
    record Failure<T>(String details) implements Response<T> {

        /**
         * @param details explanation of the failure; must not be {@code null} or blank
         * @throws IllegalArgumentException if {@code details} is {@code null} or blank
         */
        public Failure {
            if (details == null || details.isBlank()) {
                throw new IllegalArgumentException("details are required to describe a failure");
            }
        }

        @Override
        public Status status() {
            return Status.EXCEPTION;
        }

        @Override
        public Optional<T> value() {
            return Optional.empty();
        }
    }
}
