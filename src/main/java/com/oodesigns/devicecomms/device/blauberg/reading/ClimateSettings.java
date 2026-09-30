package com.oodesigns.devicecomms.device.blauberg.reading;

import com.oodesigns.devicecomms.device.blauberg.value.MvhrTemperatureCelsius;
import com.oodesigns.devicecomms.device.blauberg.value.SupplyAirTargetCelsius;
import java.util.Objects;
import java.util.Optional;

public record ClimateSettings(Optional<MvhrTemperatureCelsius> selectedTemperature,
                              Optional<MvhrTemperatureCelsius> supplyAirTemperature,
                              Optional<MvhrTemperatureCelsius> outdoorTemperature,
                              Optional<SupplyAirTargetCelsius> calculatedSupplyAirTarget,
                              ActiveWeeklyTemperature activeWeeklyTemperature) {
    public ClimateSettings {
        Objects.requireNonNull(selectedTemperature, "selectedTemperature");
        Objects.requireNonNull(supplyAirTemperature, "supplyAirTemperature");
        Objects.requireNonNull(outdoorTemperature, "outdoorTemperature");
        Objects.requireNonNull(calculatedSupplyAirTarget, "calculatedSupplyAirTarget");
        Objects.requireNonNull(activeWeeklyTemperature, "activeWeeklyTemperature");
    }
}