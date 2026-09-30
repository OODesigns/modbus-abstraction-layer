package com.oodesigns.devicecomms.device.sensors.profile;

import java.util.Objects;

public record SensorName(String value) {
    public SensorName {
        Objects.requireNonNull(value, "value");
        value = value.trim();
        if (value.isEmpty()) {
            throw new IllegalArgumentException("sensor name must not be blank");
        }
    }
}