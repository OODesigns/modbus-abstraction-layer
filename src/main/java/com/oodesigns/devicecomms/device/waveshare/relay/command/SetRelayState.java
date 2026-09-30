package com.oodesigns.devicecomms.device.waveshare.relay.command;

import com.oodesigns.devicecomms.device.waveshare.relay.value.RelayChannelNumber;
import com.oodesigns.devicecomms.device.waveshare.relay.value.RelayState;
import com.oodesigns.devicecomms.domain.device.DeviceCommand;
import java.util.Objects;

public record SetRelayState(RelayChannelNumber channel, RelayState state) implements DeviceCommand {
    public SetRelayState {
        Objects.requireNonNull(channel, "channel");
        Objects.requireNonNull(state, "state");
    }
}