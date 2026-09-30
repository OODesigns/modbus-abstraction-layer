package com.oodesigns.devicecomms.device.sensors.reading;

public record ScaledSensorMeasurement(double value) {
    public ScaledSensorMeasurement {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("scaled sensor value must be finite");
        }
    }
}