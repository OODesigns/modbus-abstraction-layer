package com.oodesigns.devicecomms.device.waveshare.analogoutput.command;

import com.oodesigns.devicecomms.device.waveshare.analogoutput.value.AnalogOutputChannelNumber;
import com.oodesigns.devicecomms.device.waveshare.analogoutput.value.OutputMillivolts;
import java.util.Objects;

public record AnalogOutputSetting(AnalogOutputChannelNumber channel, OutputMillivolts value) {
    public AnalogOutputSetting {
        Objects.requireNonNull(channel, "channel");
        Objects.requireNonNull(value, "value");
    }
}