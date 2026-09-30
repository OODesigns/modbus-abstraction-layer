package com.oodesigns.devicecomms.device.blauberg.value;

public record RelativeHumidityPercent(int value) {
    public RelativeHumidityPercent {
        if (value < 0 || value > 100) {
            throw new IllegalArgumentException("relative humidity must be between 0 and 100 percent");
        }
    }
}