package com.oodesigns.devicecomms.device.blauberg.value;

public record VocPercent(int value) {
    public VocPercent {
        if (value < 0 || value > 100) {
            throw new IllegalArgumentException("VOC must be between 0 and 100 percent");
        }
    }
}