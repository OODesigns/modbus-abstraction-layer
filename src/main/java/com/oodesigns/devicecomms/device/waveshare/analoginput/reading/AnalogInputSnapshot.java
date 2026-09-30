package com.oodesigns.devicecomms.device.waveshare.analoginput.reading;

import com.oodesigns.devicecomms.domain.device.DeviceState;
import java.util.List;
import java.util.Objects;

public record AnalogInputSnapshot(List<AnalogChannelReading> channels) implements DeviceState {
    public AnalogInputSnapshot {
        Objects.requireNonNull(channels, "channels");
        channels = List.copyOf(channels);
        if (channels.size() != 8) {
            throw new IllegalArgumentException("snapshot must contain exactly eight channels");
        }
        for (int channelIndex = 0; channelIndex < channels.size(); channelIndex++) {
            if (channels.get(channelIndex).channel().value() != channelIndex + 1) {
                throw new IllegalArgumentException("snapshot channels must be ordered from 1 through 8");
            }
        }
    }
}