package com.oodesigns.devicecomms.device.waveshare.analogoutput.reading;

import com.oodesigns.devicecomms.device.waveshare.analogoutput.value.AnalogOutputChannelNumber;
import com.oodesigns.devicecomms.device.waveshare.analogoutput.value.OutputMillivolts;
import java.util.Objects;

public record AnalogOutputReading(AnalogOutputChannelNumber channel, OutputMillivolts value) {
    public AnalogOutputReading {
        Objects.requireNonNull(channel, "channel");
        Objects.requireNonNull(value, "value");
    }
}