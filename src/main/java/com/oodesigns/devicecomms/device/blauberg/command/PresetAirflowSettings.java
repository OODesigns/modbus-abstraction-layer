package com.oodesigns.devicecomms.device.blauberg.command;

import com.oodesigns.devicecomms.device.blauberg.value.AirflowRate;
import com.oodesigns.devicecomms.device.blauberg.value.FanPreset;
import java.util.Objects;

public record PresetAirflowSettings(FanPreset preset, AirflowRate supply, AirflowRate extract) {
    public PresetAirflowSettings {
        Objects.requireNonNull(preset, "preset");
        Objects.requireNonNull(supply, "supply");
        Objects.requireNonNull(extract, "extract");
    }
}