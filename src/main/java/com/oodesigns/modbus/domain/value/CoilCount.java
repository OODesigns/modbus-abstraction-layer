package com.oodesigns.modbus.domain.value;

public record CoilCount(int value) {
    public CoilCount {
        if (value < 1 || value > 2000) {
            throw new IllegalArgumentException("value must be between 1 and 2000");
        }
    }
}