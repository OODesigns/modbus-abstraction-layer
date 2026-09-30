package com.oodesigns.devicecomms.device.waveshare.analoginput.value;

public record CurrentMicroamps(int value) implements AnalogMeasurement {
    public CurrentMicroamps {
        if (value < 0 || value > 20000) {
            throw new IllegalArgumentException("current reading must be between 0 and 20000 microamps");
        }
    }
}