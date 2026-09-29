package com.oodesigns.modbus.domain.value;

import java.util.Objects;

public record DependencyKey(String value) {
    public DependencyKey {
        Objects.requireNonNull(value, "value");
        if (value.isBlank()) {
            throw new IllegalArgumentException("value must not be blank");
        }
    }
}