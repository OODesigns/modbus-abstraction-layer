package com.oodesigns.devicecomms.device.blauberg.command;

import com.oodesigns.devicecomms.domain.device.DeviceCommand;
import java.util.Objects;

public record SetPresetAirflow(PresetAirflowSettings settings) implements DeviceCommand {
    public SetPresetAirflow {
        Objects.requireNonNull(settings, "settings");
    }
}