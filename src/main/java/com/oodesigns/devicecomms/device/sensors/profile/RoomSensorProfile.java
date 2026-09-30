package com.oodesigns.devicecomms.device.sensors.profile;

import java.util.List;
import java.util.Objects;

public record RoomSensorProfile(SensorChannelProfile temperature,
                                SensorChannelProfile humidity,
                                SensorChannelProfile particulate,
                                SensorChannelProfile carbonDioxide,
                                ParticulateRange particulateRange,
                                ParticulateSize particulateSize,
                                CarbonDioxideRange carbonDioxideRange) {
    public RoomSensorProfile {
        Objects.requireNonNull(temperature, "temperature");
        Objects.requireNonNull(humidity, "humidity");
        Objects.requireNonNull(particulate, "particulate");
        Objects.requireNonNull(carbonDioxide, "carbonDioxide");
        Objects.requireNonNull(particulateRange, "particulateRange");
        Objects.requireNonNull(particulateSize, "particulateSize");
        Objects.requireNonNull(carbonDioxideRange, "carbonDioxideRange");
        final List<SensorChannelProfile> channels = List.of(temperature, humidity, particulate, carbonDioxide);
        if (channels.stream().map(SensorChannelProfile::channel).distinct().count() != channels.size()) {
            throw new IllegalArgumentException("each room sensor must use a distinct analog channel");
        }
        requireRange(temperature, 0, 50, "temperature");
        requireRange(humidity, 0, 100, "humidity");
        requireRange(particulate, 0, particulateRange.maximumMicrogramsPerCubicMetre(), "particulate");
        requireRange(carbonDioxide, 0, carbonDioxideRange.maximumPpm(), "carbon dioxide");
    }

    private static void requireRange(final SensorChannelProfile channel,
                                     final double minimum, final double maximum,
                                     final String measurementName) {
        final SensorMeasurementRange range = channel.scale().measurementRange();
        if (range.minimum() != minimum || range.maximum() != maximum) {
            throw new IllegalArgumentException(measurementName + " calibration range does not match its sensor range");
        }
    }
}