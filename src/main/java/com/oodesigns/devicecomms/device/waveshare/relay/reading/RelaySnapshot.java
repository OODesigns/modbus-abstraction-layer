package com.oodesigns.devicecomms.device.waveshare.relay.reading;

import com.oodesigns.devicecomms.domain.device.DeviceState;
import java.util.List;
import java.util.Objects;

public record RelaySnapshot(List<RelayReading> relays) implements DeviceState {
    public RelaySnapshot {
        Objects.requireNonNull(relays, "relays");
        relays = List.copyOf(relays);
        if (relays.size() != 30) {
            throw new IllegalArgumentException("relay snapshot must contain exactly thirty channels");
        }
        for (int index = 0; index < relays.size(); index++) {
            if (relays.get(index).channel().value() != index + 1) {
                throw new IllegalArgumentException("relay snapshot channels must be ordered from 1 through 30");
            }
        }
    }
}