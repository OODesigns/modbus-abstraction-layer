package com.oodesigns.devicecomms.device.sensors;

import com.oodesigns.devicecomms.device.sensors.profile.SensorChannelProfile;
import com.oodesigns.devicecomms.device.sensors.profile.SensorMeasurementRange;
import com.oodesigns.devicecomms.device.sensors.reading.RoomHumidityPercent;
import com.oodesigns.devicecomms.device.sensors.reading.SensorChannelReader;
import com.oodesigns.devicecomms.device.sensors.reading.SensorReading;
import com.oodesigns.devicecomms.device.waveshare.analoginput.port.AnalogInputReader;
import com.oodesigns.devicecomms.domain.Response;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public final class HumiditySensor {
    private final AnalogInputReader inputReader;
    private final SensorChannelProfile channel;

    public HumiditySensor(final AnalogInputReader inputReader, final SensorChannelProfile channel) {
        this.inputReader = Objects.requireNonNull(inputReader, "inputReader");
        this.channel = Objects.requireNonNull(channel, "channel");
        final SensorMeasurementRange range = channel.scale().measurementRange();
        if (range.minimum() != 0 || range.maximum() != 100) {
            throw new IllegalArgumentException("humidity channel scale must be 0 to 100 percent");
        }
    }

    public CompletableFuture<Response<SensorReading<RoomHumidityPercent>>> read() {
        return SensorChannelReader.read(inputReader, channel).thenApply(result -> result.flatMap(measurement -> {
            try {
                return Response.success(new SensorReading<>(channel.identity(),
                        new RoomHumidityPercent(measurement.value())));
            } catch (final IllegalArgumentException exception) {
                return Response.failure(exception.getMessage());
            }
        }));
    }

    AnalogInputReader inputReader() {
        return inputReader;
    }
}