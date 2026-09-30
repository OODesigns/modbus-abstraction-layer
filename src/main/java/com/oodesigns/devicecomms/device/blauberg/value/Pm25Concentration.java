package com.oodesigns.devicecomms.device.blauberg.value;

public record Pm25Concentration(int value) {
    public Pm25Concentration {
        if (value < 0 || value > 1000) {
            throw new IllegalArgumentException("PM2.5 must be between 0 and 1000 micrograms per cubic metre");
        }
    }
}