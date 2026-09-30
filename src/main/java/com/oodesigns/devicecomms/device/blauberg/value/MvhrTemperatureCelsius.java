package com.oodesigns.devicecomms.device.blauberg.value;

public record MvhrTemperatureCelsius(double value) {
    public MvhrTemperatureCelsius {
        if (!Double.isFinite(value) || value < -50.0 || value > 100.0) {
            throw new IllegalArgumentException("MVHR temperature must be between -50 and 100 Celsius");
        }
    }
}