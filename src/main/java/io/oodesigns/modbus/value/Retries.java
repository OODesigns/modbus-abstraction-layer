package io.oodesigns.modbus.value;
public record Retries(int value) { public Retries { if (value < 0) throw new IllegalArgumentException("retries must not be negative"); } }
