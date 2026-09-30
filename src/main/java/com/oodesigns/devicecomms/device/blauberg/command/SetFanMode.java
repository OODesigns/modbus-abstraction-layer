package com.oodesigns.devicecomms.device.blauberg.command;

import com.oodesigns.devicecomms.device.blauberg.value.FanMode;
import com.oodesigns.devicecomms.domain.device.DeviceCommand;
import java.util.Objects;

public record SetFanMode(FanMode mode) implements DeviceCommand {
    public SetFanMode {
        Objects.requireNonNull(mode, "mode");
    }
}