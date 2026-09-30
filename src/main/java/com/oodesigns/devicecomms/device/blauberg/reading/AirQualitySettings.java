package com.oodesigns.devicecomms.device.blauberg.reading;

import com.oodesigns.devicecomms.device.blauberg.value.CarbonDioxidePpm;
import com.oodesigns.devicecomms.device.blauberg.value.Pm25Concentration;
import com.oodesigns.devicecomms.device.blauberg.value.RelativeHumidityPercent;
import com.oodesigns.devicecomms.device.blauberg.value.VocPercent;
import java.util.Objects;
import java.util.Optional;

public record AirQualitySettings(Optional<RelativeHumidityPercent> internalHumidity,
                                 Optional<CarbonDioxidePpm> internalCarbonDioxide,
                                 Optional<Pm25Concentration> internalPm25,
                                 Optional<VocPercent> internalVoc) {
    public AirQualitySettings {
        Objects.requireNonNull(internalHumidity, "internalHumidity");
        Objects.requireNonNull(internalCarbonDioxide, "internalCarbonDioxide");
        Objects.requireNonNull(internalPm25, "internalPm25");
        Objects.requireNonNull(internalVoc, "internalVoc");
    }
}