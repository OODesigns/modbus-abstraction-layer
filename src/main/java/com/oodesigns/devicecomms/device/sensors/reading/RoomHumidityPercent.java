package com.oodesigns.devicecomms.device.sensors.reading;

public record RoomHumidityPercent(double value) {
    public RoomHumidityPercent {
        if (!Double.isFinite(value) || value < 0 || value > 100) {
            throw new IllegalArgumentException("room humidity must be between 0 and 100 percent");
        }
    }
}