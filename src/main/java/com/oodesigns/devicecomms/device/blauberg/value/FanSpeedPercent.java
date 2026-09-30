package com.oodesigns.devicecomms.device.blauberg.value;

public record FanSpeedPercent(int value) {
    public FanSpeedPercent {
        if (value < 0 || value > 100) {
            throw new IllegalArgumentException("fan speed must be between 0 and 100 percent");
        }
    }
}