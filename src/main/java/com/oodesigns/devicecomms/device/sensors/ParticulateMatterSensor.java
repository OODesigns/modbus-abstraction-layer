package com.oodesigns.devicecomms.device.sensors;

import com.oodesigns.devicecomms.device.sensors.profile.ParticulateRange;
import com.oodesigns.devicecomms.device.sensors.profile.ParticulateSize;
import com.oodesigns.devicecomms.device.sensors.profile.SensorChannelProfile;
import com.oodesigns.devicecomms.device.sensors.profile.SensorMeasurementRange;
import com.oodesigns.devicecomms.device.sensors.reading.ParticulateMatterReading;
import com.oodesigns.devicecomms.device.sensors.reading.SensorChannelReader;
import com.oodesigns.devicecomms.device.sensors.reading.SensorReading;
import com.oodesigns.devicecomms.device.waveshare.analoginput.port.AnalogInputReader;
import com.oodesigns.devicecomms.domain.Response;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public final class ParticulateMatterSensor {
    private final AnalogInputReader inputReader;
    private final SensorChannelProfile channel;
    private final ParticulateRange range;
    private final ParticulateSize particleSize;

    public ParticulateMatterSensor(final AnalogInputReader inputReader,
                                   final SensorChannelProfile channel,
                                   final ParticulateRange range,
                                   final ParticulateSize particleSize) {
        this.inputReader = Objects.requireNonNull(inputReader, "inputReader");
        this.channel = Objects.requireNonNull(channel, "channel");
        this.range = Objects.requireNonNull(range, "range");
        this.particleSize = Objects.requireNonNull(particleSize, "particleSize");
        final SensorMeasurementRange measurementRange = channel.scale().measurementRange();
        if (measurementRange.minimum() != 0
                || measurementRange.maximum() != range.maximumMicrogramsPerCubicMetre()) {
            throw new IllegalArgumentException("particulate channel scale must match the configured range");
        }
    }

    public CompletableFuture<Response<SensorReading<ParticulateMatterReading>>> read() {
        return SensorChannelReader.read(inputReader, channel).thenApply(result -> result.flatMap(measurement -> {
            try {
                return Response.success(new SensorReading<>(channel.identity(),
                        new ParticulateMatterReading(measurement.value(), range, particleSize)));
            } catch (final IllegalArgumentException exception) {
                return Response.failure(exception.getMessage());
            }
        }));
    }

    AnalogInputReader inputReader() {
        return inputReader;
    }
}