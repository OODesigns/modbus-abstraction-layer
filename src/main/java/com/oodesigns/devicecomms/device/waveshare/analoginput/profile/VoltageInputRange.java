package com.oodesigns.devicecomms.device.waveshare.analoginput.profile;

import com.oodesigns.devicecomms.device.waveshare.analoginput.value.VoltageMillivolts;

public record VoltageInputRange(int minimumMillivolts, int maximumMillivolts) {
    public VoltageInputRange {
        if (minimumMillivolts < 0 || maximumMillivolts < minimumMillivolts || maximumMillivolts > 10000) {
            throw new IllegalArgumentException("voltage range is invalid");
        }
    }

    public boolean contains(final VoltageMillivolts value) {
        return value.value() >= minimumMillivolts && value.value() <= maximumMillivolts;
    }
}