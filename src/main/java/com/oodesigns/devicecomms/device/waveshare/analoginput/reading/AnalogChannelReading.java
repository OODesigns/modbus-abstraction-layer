package com.oodesigns.devicecomms.device.waveshare.analoginput.reading;

import com.oodesigns.devicecomms.device.waveshare.analoginput.value.AnalogChannelNumber;
import com.oodesigns.devicecomms.device.waveshare.analoginput.value.AnalogInputRangeMode;
import com.oodesigns.devicecomms.device.waveshare.analoginput.value.AnalogMeasurement;

import java.util.Objects;

public record AnalogChannelReading(AnalogChannelNumber channel,
                                  AnalogInputRangeMode rangeMode,
                                  AnalogMeasurement measurement) {
    public AnalogChannelReading {
        Objects.requireNonNull(channel, "channel");
        Objects.requireNonNull(rangeMode, "rangeMode");
        Objects.requireNonNull(measurement, "measurement");
    }
}