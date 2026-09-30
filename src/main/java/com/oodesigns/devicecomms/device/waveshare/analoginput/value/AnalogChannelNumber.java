package com.oodesigns.devicecomms.device.waveshare.analoginput.value;

public record AnalogChannelNumber(int value) {
    public AnalogChannelNumber {
        if (value < 1 || value > 8) {
            throw new IllegalArgumentException("analog channel number must be from 1 to 8");
        }
    }

}