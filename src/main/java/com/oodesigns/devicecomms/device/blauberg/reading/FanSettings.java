package com.oodesigns.devicecomms.device.blauberg.reading;

import com.oodesigns.devicecomms.device.blauberg.value.AirflowRate;
import com.oodesigns.devicecomms.device.blauberg.value.DuctPressurePascal;
import com.oodesigns.devicecomms.device.blauberg.value.FanMode;
import com.oodesigns.devicecomms.device.blauberg.value.FanRpm;
import com.oodesigns.devicecomms.device.blauberg.value.ScheduledFanSpeed;
import java.util.Objects;

public record FanSettings(FanMode mode,
                          ScheduledFanSpeed weeklySpeed,
                          FanRpm supplyRpm,
                          FanRpm extractRpm,
                          AirflowRate supplyAirflow,
                          AirflowRate extractAirflow,
                          DuctPressurePascal supplyPressure,
                          DuctPressurePascal extractPressure) {
    public FanSettings {
        Objects.requireNonNull(mode, "mode");
        Objects.requireNonNull(weeklySpeed, "weeklySpeed");
        Objects.requireNonNull(supplyRpm, "supplyRpm");
        Objects.requireNonNull(extractRpm, "extractRpm");
        Objects.requireNonNull(supplyAirflow, "supplyAirflow");
        Objects.requireNonNull(extractAirflow, "extractAirflow");
        Objects.requireNonNull(supplyPressure, "supplyPressure");
        Objects.requireNonNull(extractPressure, "extractPressure");
    }
}