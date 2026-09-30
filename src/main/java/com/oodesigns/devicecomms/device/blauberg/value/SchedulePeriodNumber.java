package com.oodesigns.devicecomms.device.blauberg.value;

public record SchedulePeriodNumber(int value) {
    public SchedulePeriodNumber {
        if (value < 1 || value > 4) {
            throw new IllegalArgumentException("schedule period must be between 1 and 4");
        }
    }
}