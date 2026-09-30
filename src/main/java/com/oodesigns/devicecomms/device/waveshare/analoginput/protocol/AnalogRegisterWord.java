package com.oodesigns.devicecomms.device.waveshare.analoginput.protocol;

public record AnalogRegisterWord(int value) {
    public AnalogRegisterWord {
        if (value < 0 || value > 65535) {
            throw new IllegalArgumentException("Modbus register word must be unsigned 16-bit");
        }
    }
}