package com.oodesigns.devicecomms.device.waveshare.relay.reading;

import com.oodesigns.devicecomms.device.waveshare.relay.value.RelayChannelNumber;
import com.oodesigns.devicecomms.device.waveshare.relay.value.RelayState;
import java.util.Objects;

public record RelayReading(RelayChannelNumber channel, RelayState state) {
    public RelayReading {
        Objects.requireNonNull(channel, "channel");
        Objects.requireNonNull(state, "state");
    }
}