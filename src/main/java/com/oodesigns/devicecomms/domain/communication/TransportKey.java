package com.oodesigns.devicecomms.domain.communication;

import java.util.Objects;

public record TransportKey(String value) {
    public TransportKey {
        Objects.requireNonNull(value, "value");
        if (value.isBlank()) {
            throw new IllegalArgumentException("value must not be blank");
        }
    }
}