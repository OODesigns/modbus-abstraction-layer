package com.oodesigns.modbus.domain.value;

public record Port(int value) {
    public Port {
        if (value < 1 || value > 65535) {
            throw new IllegalArgumentException("value must be between 1 and 65535");
        }
    }
}