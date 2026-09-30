package com.oodesigns.devicecomms.device.sensors.reading;

public record RoomTemperatureCelsius(double value) {
    public RoomTemperatureCelsius {
        if (!Double.isFinite(value) || value < 0 || value > 50) {
            throw new IllegalArgumentException("room temperature must be between 0 and 50 Celsius");
        }
    }
}