package com.oodesigns.devicecomms.domain.value;

public record UnitId(int value) {
    public UnitId {
        if (value < 0 || value > 247) {
            throw new IllegalArgumentException("value must be between 0 and 247");
        }
    }
}