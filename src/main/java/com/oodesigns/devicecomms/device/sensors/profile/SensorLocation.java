package com.oodesigns.devicecomms.device.sensors.profile;

import java.util.Objects;

public record SensorLocation(String value) {
    public SensorLocation {
        Objects.requireNonNull(value, "value");
        value = value.trim();
        if (value.isEmpty()) {
            throw new IllegalArgumentException("sensor location must not be blank");
        }
    }
}