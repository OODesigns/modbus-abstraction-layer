package com.oodesigns.devicecomms.device.waveshare.relay.command;

import com.oodesigns.devicecomms.domain.device.DeviceCommand;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public record SetRelayStates(List<RelaySetting> settings) implements DeviceCommand {
    public SetRelayStates {
        Objects.requireNonNull(settings, "settings");
        settings = List.copyOf(settings);
        if (settings.isEmpty()) {
            throw new IllegalArgumentException("at least one relay setting is required");
        }
        final Set<Integer> channels = new HashSet<>();
        if (settings.stream().anyMatch(setting -> !channels.add(setting.channel().value()))) {
            throw new IllegalArgumentException("each relay channel may be set only once per command");
        }
    }
}