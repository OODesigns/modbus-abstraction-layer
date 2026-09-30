package com.oodesigns.devicecomms.device.blauberg.value;

public record FanRpm(int value) {
    public FanRpm {
        if (value < 0 || value > 5000) {
            throw new IllegalArgumentException("fan speed must be between 0 and 5000 RPM");
        }
    }
}