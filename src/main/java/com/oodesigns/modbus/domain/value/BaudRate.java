package com.oodesigns.modbus.domain.value;

public record BaudRate(int value) {
    public BaudRate {
        if (value < 1) {
            throw new IllegalArgumentException("value must be positive");
        }
    }
}