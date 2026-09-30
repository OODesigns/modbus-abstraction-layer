package com.oodesigns.devicecomms.device.waveshare.relay.command;

import com.oodesigns.devicecomms.device.waveshare.relay.value.RelayChannelNumber;
import com.oodesigns.devicecomms.device.waveshare.relay.value.RelayState;
import java.util.Objects;

public record RelaySetting(RelayChannelNumber channel, RelayState state) {
    public RelaySetting {
        Objects.requireNonNull(channel, "channel");
        Objects.requireNonNull(state, "state");
    }
}