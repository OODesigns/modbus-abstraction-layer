package com.oodesigns.devicecomms.domain.value;

public record StartAddress(int value) {
    public StartAddress {
        if (value < 0 || value > 65535) {
            throw new IllegalArgumentException("value must be between 0 and 65535");
        }
    }
}