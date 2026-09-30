package com.oodesigns.devicecomms.device.sensors.profile;

public record SensorMeasurementRange(double minimum, double maximum) {
    public SensorMeasurementRange {
        if (!Double.isFinite(minimum) || !Double.isFinite(maximum) || maximum <= minimum) {
            throw new IllegalArgumentException("sensor measurement range is invalid");
        }
    }
}