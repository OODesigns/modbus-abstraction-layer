package com.oodesigns.devicecomms.domain.value;

public record RegisterCount(int value) {
    public RegisterCount {
        if (value < 1 || value > 125) {
            throw new IllegalArgumentException("value must be between 1 and 125");
        }
    }
}