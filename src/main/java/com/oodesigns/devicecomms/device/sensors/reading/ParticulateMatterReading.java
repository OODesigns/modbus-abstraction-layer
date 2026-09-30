package com.oodesigns.devicecomms.device.sensors.reading;

import com.oodesigns.devicecomms.device.sensors.profile.ParticulateRange;
import com.oodesigns.devicecomms.device.sensors.profile.ParticulateSize;
import java.util.Objects;

public record ParticulateMatterReading(double value, ParticulateRange range, ParticulateSize particleSize) {
    public ParticulateMatterReading {
        Objects.requireNonNull(range, "range");
        Objects.requireNonNull(particleSize, "particleSize");
        if (!Double.isFinite(value) || value < 0 || value > range.maximumMicrogramsPerCubicMetre()) {
            throw new IllegalArgumentException("particulate reading is outside its configured range");
        }
    }
}