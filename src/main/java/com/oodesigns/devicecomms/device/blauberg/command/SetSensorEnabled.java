package com.oodesigns.devicecomms.device.blauberg.command;

import com.oodesigns.devicecomms.domain.device.DeviceCommand;
import java.util.Objects;

public record SetSensorEnabled(Sensor sensor, boolean enabled) implements DeviceCommand {
    public SetSensorEnabled {
        Objects.requireNonNull(sensor, "sensor");
    }
}