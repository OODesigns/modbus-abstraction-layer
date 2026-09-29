package com.oodesigns.modbus.domain.value;

public record Retries(int value) {
    public Retries {
        if (value < 0) {
            throw new IllegalArgumentException("value must not be negative");
        }
    }
}