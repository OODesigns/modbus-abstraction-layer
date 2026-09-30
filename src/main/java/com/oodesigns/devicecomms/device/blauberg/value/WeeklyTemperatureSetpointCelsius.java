package com.oodesigns.devicecomms.device.blauberg.value;

public record WeeklyTemperatureSetpointCelsius(double value) {
    public WeeklyTemperatureSetpointCelsius {
        if (!Double.isFinite(value) || value < 15.0 || value > 30.0 || value != Math.rint(value)) {
            throw new IllegalArgumentException("weekly temperature setpoint must be a whole degree from 15 to 30 Celsius");
        }
    }
}