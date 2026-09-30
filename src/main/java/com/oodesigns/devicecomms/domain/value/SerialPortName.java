package com.oodesigns.devicecomms.domain.value;

import java.util.Objects;

public record SerialPortName(String value) {
    public SerialPortName {
        Objects.requireNonNull(value, "value");
        if (value.isBlank()) {
            throw new IllegalArgumentException("value must not be blank");
        }
    }
}