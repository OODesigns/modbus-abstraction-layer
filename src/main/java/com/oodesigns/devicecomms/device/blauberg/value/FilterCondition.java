package com.oodesigns.devicecomms.device.blauberg.value;

public record FilterCondition(int value) {
    public FilterCondition {
        if (value < 0 || value > 3) {
            throw new IllegalArgumentException("filter condition must be between 0 and 3");
        }
    }
}