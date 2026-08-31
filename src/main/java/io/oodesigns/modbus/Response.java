package io.oodesigns.modbus;

import java.util.Objects;
import java.util.function.Function;

/** The result of a domain operation. A failure is propagated unchanged by map and flatMap. */
public sealed interface Response<T> permits Success, Failure {
    Status status();
    String details();
    T value();

    static <T> Response<T> success(T value) { return new Success<>(value, ""); }
    static <T> Response<T> success(T value, String details) { return new Success<>(value, details); }
    static <T> Response<T> failure(String details) { return new Failure<>(details); }

    default <R> Response<R> map(Function<? super T, ? extends R> mapper) {
        if (mapper == null) return Response.failure("mapper is required");
        if (this instanceof Failure<T> failure) return failure.cast();
        try { return Response.success(mapper.apply(value())); }
        catch (RuntimeException exception) { return Response.failure(exception.getMessage() == null ? exception.getClass().getSimpleName() : exception.getMessage()); }
    }
    default <R> Response<R> flatMap(Function<? super T, Response<R>> mapper) {
        if (mapper == null) return Response.failure("mapper is required");
        if (this instanceof Failure<T> failure) return failure.cast();
        try { return Objects.requireNonNull(mapper.apply(value()), "mapper response"); }
        catch (RuntimeException exception) { return Response.failure(exception.getMessage() == null ? exception.getClass().getSimpleName() : exception.getMessage()); }
    }
    default <R> R fold(Function<? super Failure<T>, ? extends R> onFailure, Function<? super T, ? extends R> onSuccess) {
        if (onFailure == null || onSuccess == null) return null;
        try { return this instanceof Failure<T> failure ? onFailure.apply(failure) : onSuccess.apply(value()); }
        catch (RuntimeException ignored) { return null; }
    }
}
