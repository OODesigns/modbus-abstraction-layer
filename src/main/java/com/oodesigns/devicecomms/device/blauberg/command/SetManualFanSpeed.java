package com.oodesigns.devicecomms.device.blauberg.command;

import com.oodesigns.devicecomms.device.blauberg.value.FanSpeedPercent;
import com.oodesigns.devicecomms.domain.device.DeviceCommand;
import java.util.Objects;

public record SetManualFanSpeed(FanSpeedPercent speed) implements DeviceCommand {
    public SetManualFanSpeed {
        Objects.requireNonNull(speed, "speed");
    }
}