package com.oodesigns.devicecomms.device.sensors.profile;

import java.util.Objects;

public record SensorIdentity(SensorName name, SensorLocation location) {
    public SensorIdentity {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(location, "location");
    }
}