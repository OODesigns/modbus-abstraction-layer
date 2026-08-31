package io.oodesigns.modbus;
public record Success<T>(T value, String details) implements Response<T> {
    public Success { if (details == null) throw new IllegalArgumentException("details is required"); }
    @Override public Status status() { return Status.OK; }
}
