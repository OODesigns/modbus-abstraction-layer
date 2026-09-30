package com.oodesigns.devicecomms.device.sensors.profile;

public record RawSignalValue(int value) {
    public RawSignalValue {
        if (value < 0 || value > 20000) {
            throw new IllegalArgumentException("raw signal value is outside the supported range");
        }
    }
}