package com.oodesigns.modbus.domain.value;

import java.time.Duration;
import java.util.Objects;

public record Timeout(Duration value) {
    public Timeout {
        Objects.requireNonNull(value, "value");
        if (value.isZero() || value.isNegative()) {
            throw new IllegalArgumentException("value must be positive");
        }
    }
}