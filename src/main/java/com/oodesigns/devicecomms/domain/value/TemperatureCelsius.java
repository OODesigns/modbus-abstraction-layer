package com.oodesigns.devicecomms.domain.value;

public record TemperatureCelsius(double value) {
    public static final double LOW = -273.15;
    public static final double HIGH = 1000.0;

    public TemperatureCelsius {
        if (!Double.isFinite(value) || value < LOW || value > HIGH) {
            throw new IllegalArgumentException("value must be between -273.15 and 1000.0 Celsius");
        }
    }
}