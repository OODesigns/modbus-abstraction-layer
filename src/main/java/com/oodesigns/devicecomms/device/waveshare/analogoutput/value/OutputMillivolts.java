package com.oodesigns.devicecomms.device.waveshare.analogoutput.value;

public record OutputMillivolts(int value) {
    public OutputMillivolts {
        if (value < 0 || value > 10000) {
            throw new IllegalArgumentException("analog output must be from 0 to 10000 millivolts");
        }
    }
}