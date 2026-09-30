package com.oodesigns.devicecomms.device.blauberg.reading;

import com.oodesigns.devicecomms.domain.device.DeviceState;
import java.util.Objects;

public record MvhrSnapshot(OperationSettings operation,
                           FanSettings fan,
                           MaintenanceSettings maintenance,
                           ClimateSettings climate,
                           AirQualitySettings airQuality) implements DeviceState {
    public MvhrSnapshot {
        Objects.requireNonNull(operation, "operation");
        Objects.requireNonNull(fan, "fan");
        Objects.requireNonNull(maintenance, "maintenance");
        Objects.requireNonNull(climate, "climate");
        Objects.requireNonNull(airQuality, "airQuality");
    }
}