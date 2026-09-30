package com.oodesigns.devicecomms.device.blauberg.value;

public record DuctPressurePascal(int value) {
    public DuctPressurePascal {
        if (value < 0 || value > 10000) {
            throw new IllegalArgumentException("duct pressure must be between 0 and 10000 pascals");
        }
    }
}