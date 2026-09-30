package com.oodesigns.devicecomms.device.sensors.reading;

import com.oodesigns.devicecomms.device.sensors.profile.CarbonDioxideRange;
import java.util.Objects;

public record CarbonDioxideReadingPpm(double value, CarbonDioxideRange range) {
    public CarbonDioxideReadingPpm {
        Objects.requireNonNull(range, "range");
        if (!Double.isFinite(value) || value < 0 || value > range.maximumPpm()) {
            throw new IllegalArgumentException("CO2 reading is outside its configured range");
        }
    }
}