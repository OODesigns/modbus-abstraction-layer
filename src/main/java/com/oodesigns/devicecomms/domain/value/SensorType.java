package com.oodesigns.devicecomms.domain.value;

import java.util.Objects;

public record SensorType(String value) {
    public SensorType {
        Objects.requireNonNull(value, "value");
        if (value.isBlank()) {
            throw new IllegalArgumentException("value must not be blank");
        }
    }
}