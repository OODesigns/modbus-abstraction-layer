package com.oodesigns.devicecomms.device.waveshare.analogoutput.value;

public record AnalogOutputChannelNumber(int value) {
    public AnalogOutputChannelNumber {
        if (value < 1 || value > 8) {
            throw new IllegalArgumentException("analog output channel must be from 1 to 8");
        }
    }
}