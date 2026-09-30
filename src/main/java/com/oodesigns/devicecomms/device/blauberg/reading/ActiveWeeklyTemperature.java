package com.oodesigns.devicecomms.device.blauberg.reading;

import com.oodesigns.devicecomms.device.blauberg.value.WeeklyTemperatureMode;
import com.oodesigns.devicecomms.device.blauberg.value.WeeklyTemperatureSetpointCelsius;
import java.util.Objects;
import java.util.Optional;

public record ActiveWeeklyTemperature(WeeklyTemperatureMode mode,
                                      Optional<WeeklyTemperatureSetpointCelsius> setpoint) {
    public ActiveWeeklyTemperature {
        Objects.requireNonNull(mode, "mode");
        Objects.requireNonNull(setpoint, "setpoint");
        if ((mode == WeeklyTemperatureMode.SETPOINT) != setpoint.isPresent()) {
            throw new IllegalArgumentException("temperature mode and setpoint must agree");
        }
    }

    public static ActiveWeeklyTemperature ventilationOnly() {
        return new ActiveWeeklyTemperature(WeeklyTemperatureMode.VENTILATION_ONLY, Optional.empty());
    }

    public static ActiveWeeklyTemperature setpoint(final WeeklyTemperatureSetpointCelsius setpoint) {
        return new ActiveWeeklyTemperature(WeeklyTemperatureMode.SETPOINT,
                Optional.of(Objects.requireNonNull(setpoint, "setpoint")));
    }
}