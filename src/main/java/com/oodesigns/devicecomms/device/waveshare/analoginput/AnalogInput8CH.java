package com.oodesigns.devicecomms.device.waveshare.analoginput;

import com.oodesigns.devicecomms.adapter.modbus.HoldingRegisterPoint;
import com.oodesigns.devicecomms.adapter.modbus.InputRegisterPoint;
import com.oodesigns.devicecomms.device.waveshare.analoginput.profile.AnalogInputProfile;
import com.oodesigns.devicecomms.device.waveshare.analoginput.profile.VoltageInputRange;
import com.oodesigns.devicecomms.device.waveshare.analoginput.port.AnalogInputReader;
import com.oodesigns.devicecomms.device.waveshare.analoginput.protocol.AnalogRegisterWord;
import com.oodesigns.devicecomms.device.waveshare.analoginput.query.ReadAnalogChannels;
import com.oodesigns.devicecomms.device.waveshare.analoginput.reading.AnalogChannelReading;
import com.oodesigns.devicecomms.device.waveshare.analoginput.reading.AnalogInputSnapshot;
import com.oodesigns.devicecomms.device.waveshare.analoginput.value.AdcScaleCode;
import com.oodesigns.devicecomms.device.waveshare.analoginput.value.AnalogChannelNumber;
import com.oodesigns.devicecomms.device.waveshare.analoginput.value.AnalogInputRangeMode;
import com.oodesigns.devicecomms.device.waveshare.analoginput.value.AnalogMeasurement;
import com.oodesigns.devicecomms.device.waveshare.analoginput.value.CurrentMicroamps;
import com.oodesigns.devicecomms.device.waveshare.analoginput.value.VoltageMillivolts;
import com.oodesigns.devicecomms.domain.Response;
import com.oodesigns.devicecomms.domain.communication.CommunicationClient;
import com.oodesigns.devicecomms.domain.communication.PointAddress;
import com.oodesigns.devicecomms.domain.communication.PointSnapshot;
import com.oodesigns.devicecomms.domain.device.Device;
import com.oodesigns.devicecomms.domain.device.DeviceCommand;
import com.oodesigns.devicecomms.domain.device.DeviceQuery;
import com.oodesigns.devicecomms.domain.device.DeviceState;
import com.oodesigns.devicecomms.domain.value.StartAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;

public final class AnalogInput8CH implements Device, AnalogInputReader {
    private record SensorReaderFailure(String details) {
        private SensorReaderFailure {
            if (details == null || details.isBlank()) {
                throw new IllegalArgumentException("failure details must not be blank");
            }
        }
    }

    private static final List<AnalogChannelNumber> CHANNELS = List.of(
            new AnalogChannelNumber(1), new AnalogChannelNumber(2),
            new AnalogChannelNumber(3), new AnalogChannelNumber(4),
            new AnalogChannelNumber(5), new AnalogChannelNumber(6),
            new AnalogChannelNumber(7), new AnalogChannelNumber(8));

    private final CommunicationClient client;
    private final AnalogInputProfile profile;

    public AnalogInput8CH(final CommunicationClient client, final AnalogInputProfile profile) {
        this.client = Objects.requireNonNull(client, "client");
        this.profile = Objects.requireNonNull(profile, "profile");
    }

    @Override
    public CompletableFuture<Response<Void>> open() {
        try {
            return normalize(client.connect(), new SensorReaderFailure("sensor connection could not be opened"));
        } catch (final RuntimeException exception) {
            return failure(exception, new SensorReaderFailure("sensor connection could not be opened"));
        }
    }

    @Override
    public CompletableFuture<Response<DeviceState>> read(final DeviceQuery query) {
        if (!(query instanceof ReadAnalogChannels)) {
            return completedFailure(new SensorReaderFailure("unsupported Waveshare sensor query"));
        }
        return readChannels().thenApply(result -> result.map(snapshot -> (DeviceState) snapshot));
    }

    @Override
    public CompletableFuture<Response<AnalogInputSnapshot>> readChannels() {
        final List<PointAddress<?>> requestedPoints = requestedPoints();
        try {
            return normalize(client.read(requestedPoints),
                    new SensorReaderFailure("eight-channel input read failed"))
                    .thenApply(result -> result.flatMap(this::decodeSnapshot));
        } catch (final RuntimeException exception) {
            return failure(exception, new SensorReaderFailure("eight-channel input read failed"));
        }
    }

    @Override
    public CompletableFuture<Response<Void>> execute(final DeviceCommand command) {
        return completedFailure(new SensorReaderFailure("Waveshare analog input component is read-only"));
    }

    @Override
    public CompletableFuture<Response<Void>> close() {
        try {
            return normalize(client.disconnect(), new SensorReaderFailure("sensor connection could not be closed"));
        } catch (final RuntimeException exception) {
            return failure(exception, new SensorReaderFailure("sensor connection could not be closed"));
        }
    }

    private static List<PointAddress<?>> requestedPoints() {
        final List<PointAddress<?>> points = new ArrayList<>(16);
        CHANNELS.stream().map(AnalogInput8CH::inputPoint).forEach(points::add);
        CHANNELS.stream().map(AnalogInput8CH::rangePoint).forEach(points::add);
        return List.copyOf(points);
    }

    private static InputRegisterPoint inputPoint(final AnalogChannelNumber channel) {
        return new InputRegisterPoint(new StartAddress(channel.value() - 1));
    }

    private static HoldingRegisterPoint rangePoint(final AnalogChannelNumber channel) {
        return new HoldingRegisterPoint(new StartAddress(0x1000 + channel.value() - 1));
    }

    private Response<AnalogInputSnapshot> decodeSnapshot(final PointSnapshot pointSnapshot) {
        if (pointSnapshot == null) {
            return Response.failure("Waveshare response snapshot is missing");
        }
        final List<AnalogChannelReading> readings = new ArrayList<>(CHANNELS.size());
        for (final AnalogChannelNumber channel : CHANNELS) {
            final Response<AnalogRegisterWord> input = inputWord(pointSnapshot, channel);
            final Response<AnalogRegisterWord> modeCode = modeWord(pointSnapshot, channel);
            if (input.status() != Response.Status.OK) {
                return Response.failure(input.details());
            }
            if (modeCode.status() != Response.Status.OK) {
                return Response.failure(modeCode.details());
            }
            final Response<AnalogInputRangeMode> mode = decodeRangeMode(modeCode.value());
            if (mode.status() != Response.Status.OK) {
                return Response.failure(mode.details());
            }
            final Response<AnalogChannelReading> reading = decodeChannel(channel, mode.value(), input.value());
            if (reading.status() != Response.Status.OK) {
                return Response.failure("channel " + channel.value() + ": " + reading.details());
            }
            readings.add(reading.value());
        }
        return Response.success(new AnalogInputSnapshot(readings));
    }

    private static Response<AnalogRegisterWord> inputWord(
            final PointSnapshot pointSnapshot, final AnalogChannelNumber channel) {
        return wrapWord(pointSnapshot.valueOf(inputPoint(channel)));
    }

    private static Response<AnalogRegisterWord> modeWord(
            final PointSnapshot pointSnapshot, final AnalogChannelNumber channel) {
        return wrapWord(pointSnapshot.valueOf(rangePoint(channel)));
    }

    private static Response<AnalogRegisterWord> wrapWord(final Response<Integer> rawValue) {
        return rawValue.flatMap(value -> {
            try {
                return Response.success(new AnalogRegisterWord(value));
            } catch (final IllegalArgumentException exception) {
                return Response.failure(exception.getMessage());
            }
        });
    }

    private static Response<AnalogInputRangeMode> decodeRangeMode(final AnalogRegisterWord code) {
        return switch (code.value()) {
            case 0 -> Response.success(AnalogInputRangeMode.VOLTAGE_ZERO_BASED);
            case 1 -> Response.success(AnalogInputRangeMode.VOLTAGE_OFFSET);
            case 2 -> Response.success(AnalogInputRangeMode.CURRENT_ZERO_BASED);
            case 3 -> Response.success(AnalogInputRangeMode.CURRENT_OFFSET);
            case 4 -> Response.success(AnalogInputRangeMode.ADC_SCALE_CODE);
            default -> Response.failure("unsupported Waveshare channel range code: " + code.value());
        };
    }

    private Response<AnalogChannelReading> decodeChannel(final AnalogChannelNumber channel,
                                                         final AnalogInputRangeMode mode,
                                                         final AnalogRegisterWord rawValue) {
        try {
            final AnalogMeasurement measurement;
            if (mode == AnalogInputRangeMode.VOLTAGE_ZERO_BASED
                    || mode == AnalogInputRangeMode.VOLTAGE_OFFSET) {
                measurement = decodeVoltage(mode, rawValue);
            } else if (mode == AnalogInputRangeMode.CURRENT_ZERO_BASED) {
                measurement = new CurrentMicroamps(rawValue.value());
            } else if (mode == AnalogInputRangeMode.CURRENT_OFFSET) {
                measurement = decodeOffsetCurrent(rawValue);
            } else {
                measurement = new AdcScaleCode(rawValue.value());
            }
            return Response.success(new AnalogChannelReading(channel, mode, measurement));
        } catch (final IllegalArgumentException exception) {
            return Response.failure(exception.getMessage());
        }
    }

    private AnalogMeasurement decodeVoltage(final AnalogInputRangeMode mode,
                                            final AnalogRegisterWord rawValue) {
        final VoltageMillivolts voltage = new VoltageMillivolts(rawValue.value());
        final VoltageInputRange validRange = profile.voltageRange(mode);
        if (!validRange.contains(voltage)) {
            throw new IllegalArgumentException("voltage value is outside the configured channel range");
        }
        return voltage;
    }

    private static CurrentMicroamps decodeOffsetCurrent(final AnalogRegisterWord rawValue) {
        if (rawValue.value() < 4000) {
            throw new IllegalArgumentException("4-20 mA input is below its minimum value");
        }
        return new CurrentMicroamps(rawValue.value());
    }

    private static <T> CompletableFuture<Response<T>> normalize(
            final CompletionStage<Response<T>> stage, final SensorReaderFailure fallback) {
        if (stage == null) {
            return completedFailure(fallback);
        }
        return stage.handle((result, error) -> {
            if (error != null) {
                final Throwable cause = error instanceof CompletionException && error.getCause() != null
                        ? error.getCause() : error;
                if (cause instanceof Error fatalError) {
                    throw fatalError;
                }
                return Response.<T>failure(message(cause, fallback).details());
            }
            return result == null ? Response.<T>failure(fallback.details()) : result;
        }).toCompletableFuture();
    }

    private static <T> CompletableFuture<Response<T>> failure(
            final Throwable cause, final SensorReaderFailure fallback) {
        if (cause instanceof Error fatalError) {
            throw fatalError;
        }
        return completedFailure(message(cause, fallback));
    }

    private static SensorReaderFailure message(final Throwable cause, final SensorReaderFailure fallback) {
        final String details = cause.getMessage();
        return details == null || details.isBlank() ? fallback : new SensorReaderFailure(details);
    }

    private static <T> CompletableFuture<Response<T>> completedFailure(final SensorReaderFailure failure) {
        return CompletableFuture.completedFuture(Response.failure(failure.details()));
    }
}