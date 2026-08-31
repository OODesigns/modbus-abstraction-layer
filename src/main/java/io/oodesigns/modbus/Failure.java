package io.oodesigns.modbus;
import java.util.Objects;
public record Failure<T>(String details) implements Response<T> {
    public Failure { Objects.requireNonNull(details, "details"); if (details.isBlank()) throw new IllegalArgumentException("details must not be blank"); }
    @Override public Status status() { return Status.EXCEPTION; }
    @Override public T value() { return null; }
    <R> Response<R> cast() { return new Failure<>(details); }
}
