package com.oodesigns.devicecomms.device.waveshare.analoginput.value;

public record VoltageMillivolts(int value) implements AnalogMeasurement {
    public VoltageMillivolts {
        if (value < 0 || value > 10000) {
            throw new IllegalArgumentException("voltage reading must be between 0 and 10000 mV");
        }
    }
}