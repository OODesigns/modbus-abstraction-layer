package com.oodesigns.devicecomms.device.sensors;

import com.oodesigns.devicecomms.device.sensors.profile.SensorChannelProfile;
import com.oodesigns.devicecomms.device.sensors.profile.SensorMeasurementRange;
import com.oodesigns.devicecomms.device.sensors.reading.RoomTemperatureCelsius;
import com.oodesigns.devicecomms.device.sensors.reading.SensorChannelReader;
import com.oodesigns.devicecomms.device.sensors.reading.SensorReading;
import com.oodesigns.devicecomms.device.waveshare.analoginput.port.AnalogInputReader;
import com.oodesigns.devicecomms.domain.Response;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public final class TemperatureSensor {
    private final AnalogInputReader inputReader;
    private final SensorChannelProfile channel;

    public TemperatureSensor(final AnalogInputReader inputReader, final SensorChannelProfile channel) {
        this.inputReader = Objects.requireNonNull(inputReader, "inputReader");
        this.channel = Objects.requireNonNull(channel, "channel");
        requireRange(channel, 0, 50);
    }

    public CompletableFuture<Response<SensorReading<RoomTemperatureCelsius>>> read() {
        return SensorChannelReader.read(inputReader, channel).thenApply(result -> result.flatMap(measurement -> {
            try {
                return Response.success(new SensorReading<>(channel.identity(),
                        new RoomTemperatureCelsius(measurement.value())));
            } catch (final IllegalArgumentException exception) {
                return Response.failure(exception.getMessage());
            }
        }));
    }

    AnalogInputReader inputReader() {
        return inputReader;
    }

    private static void requireRange(final SensorChannelProfile channel,
                                     final double minimum, final double maximum) {
        final SensorMeasurementRange range = channel.scale().measurementRange();
        if (range.minimum() != minimum || range.maximum() != maximum) {
            throw new IllegalArgumentException("temperature channel scale must be 0 to 50 Celsius");
        }
    }
}