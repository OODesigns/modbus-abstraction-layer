package com.oodesigns.devicecomms.device.waveshare.analoginput.value;

public record AdcScaleCode(int value) implements AnalogMeasurement {
    public AdcScaleCode {
        if (value < 0 || value > 4095) {
            throw new IllegalArgumentException("ADC scale code must be between 0 and 4095");
        }
    }
}