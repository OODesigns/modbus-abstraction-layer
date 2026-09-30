package com.oodesigns.devicecomms.device.blauberg.reading;

import com.oodesigns.devicecomms.device.blauberg.value.FilterCondition;
import java.util.Objects;

public record MaintenanceSettings(FilterCondition filterCondition) {
    public MaintenanceSettings {
        Objects.requireNonNull(filterCondition, "filterCondition");
    }
}