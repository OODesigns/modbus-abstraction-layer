package com.oodesigns.devicecomms.device.waveshare.analogoutput.command;

import com.oodesigns.devicecomms.domain.device.DeviceCommand;
import java.util.Objects;
import com.oodesigns.devicecomms.device.waveshare.analogoutput.value.AnalogOutputChannelNumber;
import com.oodesigns.devicecomms.device.waveshare.analogoutput.value.OutputMillivolts;

public record SetAnalogOutput(AnalogOutputChannelNumber channel,
                              OutputMillivolts value) implements DeviceCommand {
    public SetAnalogOutput {
        Objects.requireNonNull(channel, "channel");
        Objects.requireNonNull(value, "value");
    }
}