package com.oodesigns.devicecomms.device.sensors.reading;

import com.oodesigns.devicecomms.device.sensors.profile.SensorIdentity;
import java.util.Objects;

public record SensorReading<T>(SensorIdentity identity, T value) {
    public SensorReading {
        Objects.requireNonNull(identity, "identity");
        Objects.requireNonNull(value, "value");
    }
}