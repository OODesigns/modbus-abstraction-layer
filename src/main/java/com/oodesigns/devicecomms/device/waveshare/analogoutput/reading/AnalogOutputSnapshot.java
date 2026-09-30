package com.oodesigns.devicecomms.device.waveshare.analogoutput.reading;

import com.oodesigns.devicecomms.domain.device.DeviceState;
import java.util.List;
import java.util.Objects;

public record AnalogOutputSnapshot(List<AnalogOutputReading> channels) implements DeviceState {
    public AnalogOutputSnapshot {
        Objects.requireNonNull(channels, "channels");
        channels = List.copyOf(channels);
        if (channels.size() != 8) {
            throw new IllegalArgumentException("snapshot must contain exactly eight channels");
        }
        for (int index = 0; index < channels.size(); index++) {
            if (channels.get(index).channel().value() != index + 1) {
                throw new IllegalArgumentException("snapshot channels must be ordered from 1 through 8");
            }
        }
    }
}