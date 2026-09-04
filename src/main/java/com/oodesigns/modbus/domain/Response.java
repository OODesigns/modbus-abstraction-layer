package com.oodesigns.modbus.domain;

import java.util.Objects;
import java.util.function.Function;

public sealed interface Response<T> permits Response.Success, Response.Failure {
    Status status();

    String details();

    T value();

    default <U> Response<U> map(final Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper, "mapper");
        return switch (this) {
            case Success<T> success -> success.map(mapper);
            case Failure<T> failure -> failure.propagate();
        };
    }

    default <U> Response<U> flatMap(final Function<? super T, Response<U>> mapper) {
        Objects.requireNonNull(mapper, "mapper");
        return switch (this) {
            case Success<T> success -> success.flatMap(mapper);
            case Failure<T> failure -> failure.propagate();
        };
    }

    default <U> U fold(final Function<? super T, ? extends U> onSuccess,
                       final Function<? super Failure<T>, ? extends U> onFailure) {
        Objects.requireNonNull(onSuccess, "onSuccess");
        Objects.requireNonNull(onFailure, "onFailure");
        return switch (this) {
            case Success<T> success -> onSuccess.apply(success.value());
            case Failure<T> failure -> onFailure.apply(failure);
        };
    }

    static <T> Response<T> success(final T value) {
        return new Success<>(value);
    }

    static Response<Void> success() {
        return new Success<>(null);
    }

    static <T> Response<T> failure(final String details) {
        return new Failure<>(details);
    }

    record Success<T>(T value) implements Response<T> {
        @Override
        public Status status() {
            return Status.OK;
        }

        @Override
        public String details() {
            return "";
        }

        @Override
        public <U> Response<U> map(final Function<? super T, ? extends U> mapper) {
            return Response.success(mapper.apply(value));
        }

        @Override
        public <U> Response<U> flatMap(final Function<? super T, Response<U>> mapper) {
            return Objects.requireNonNull(mapper.apply(value), "mapper result");
        }
    }

    record Failure<T>(String details) implements Response<T> {
        public Failure {
            Objects.requireNonNull(details, "details");
            if (details.isBlank()) {
                throw new IllegalArgumentException("details must not be blank");
            }
        }

        @Override
        public Status status() {
            return Status.EXCEPTION;
        }

        @Override
        public T value() {
            return null;
        }

        @SuppressWarnings("unchecked")
        private <U> Response<U> propagate() {
            return (Response<U>) this;
        }
    }

    enum Status {
        OK,
        EXCEPTION
    }
}
