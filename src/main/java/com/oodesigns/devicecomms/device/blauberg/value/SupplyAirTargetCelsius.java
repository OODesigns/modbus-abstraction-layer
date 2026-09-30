package com.oodesigns.devicecomms.device.blauberg.value;

public record SupplyAirTargetCelsius(double value) {
    public SupplyAirTargetCelsius {
        if (!Double.isFinite(value) || value < 10.0 || value > 40.0) {
            throw new IllegalArgumentException("supply air target must be between 10 and 40 Celsius");
        }
    }
}