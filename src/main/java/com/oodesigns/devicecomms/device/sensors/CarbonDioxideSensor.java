package com.oodesigns.devicecomms.device.sensors;

import com.oodesigns.devicecomms.device.sensors.profile.CarbonDioxideRange;
import com.oodesigns.devicecomms.device.sensors.profile.SensorChannelProfile;
import com.oodesigns.devicecomms.device.sensors.profile.SensorMeasurementRange;
import com.oodesigns.devicecomms.device.sensors.reading.CarbonDioxideReadingPpm;
import com.oodesigns.devicecomms.device.sensors.reading.SensorChannelReader;
import com.oodesigns.devicecomms.device.sensors.reading.SensorReading;
import com.oodesigns.devicecomms.device.waveshare.analoginput.port.AnalogInputReader;
import com.oodesigns.devicecomms.domain.Response;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public final class CarbonDioxideSensor {
    private final AnalogInputReader inputReader;
    private final SensorChannelProfile channel;
    private final CarbonDioxideRange range;

    public CarbonDioxideSensor(final AnalogInputReader inputReader,
                               final SensorChannelProfile channel,
                               final CarbonDioxideRange range) {
        this.inputReader = Objects.requireNonNull(inputReader, "inputReader");
        this.channel = Objects.requireNonNull(channel, "channel");
        this.range = Objects.requireNonNull(range, "range");
        final SensorMeasurementRange measurementRange = channel.scale().measurementRange();
        if (measurementRange.minimum() != 0 || measurementRange.maximum() != range.maximumPpm()) {
            throw new IllegalArgumentException("CO2 channel scale must match the configured range");
        }
    }

    public CompletableFuture<Response<SensorReading<CarbonDioxideReadingPpm>>> read() {
        return SensorChannelReader.read(inputReader, channel).thenApply(result -> result.flatMap(measurement -> {
            try {
                return Response.success(new SensorReading<>(channel.identity(),
                        new CarbonDioxideReadingPpm(measurement.value(), range)));
            } catch (final IllegalArgumentException exception) {
                return Response.failure(exception.getMessage());
            }
        }));
    }

    AnalogInputReader inputReader() {
        return inputReader;
    }
}