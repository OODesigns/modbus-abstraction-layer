package com.oodesigns.devicecomms.device.blauberg.value;

public record CarbonDioxidePpm(int value) {
    public CarbonDioxidePpm {
        if (value < 0 || value > 10000) {
            throw new IllegalArgumentException("CO2 must be between 0 and 10000 ppm");
        }
    }
}