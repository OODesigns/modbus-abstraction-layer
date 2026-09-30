package com.oodesigns.devicecomms.device.waveshare.analogoutput;

import com.oodesigns.devicecomms.adapter.modbus.HoldingRegisterPoint;
import com.oodesigns.devicecomms.device.waveshare.analogoutput.command.AnalogOutputSetting;
import com.oodesigns.devicecomms.device.waveshare.analogoutput.command.SetAnalogOutput;
import com.oodesigns.devicecomms.device.waveshare.analogoutput.command.SetAnalogOutputs;
import com.oodesigns.devicecomms.device.waveshare.analogoutput.query.ReadAnalogOutputs;
import com.oodesigns.devicecomms.device.waveshare.analogoutput.query.AnalogOutputQuery;
import com.oodesigns.devicecomms.device.waveshare.analogoutput.reading.AnalogOutputReading;
import com.oodesigns.devicecomms.device.waveshare.analogoutput.reading.AnalogOutputSnapshot;
import com.oodesigns.devicecomms.device.waveshare.analogoutput.value.AnalogOutputChannelNumber;
import com.oodesigns.devicecomms.device.waveshare.analogoutput.value.OutputMillivolts;
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

public final class AnalogOutput8CH implements Device {
    private final CommunicationClient client;

    public AnalogOutput8CH(final CommunicationClient client) {
        this.client = Objects.requireNonNull(client, "client");
    }

    @Override
    public CompletableFuture<Response<Void>> open() {
        try {
            return normalize(client.connect(), "analog-output connection could not be opened");
        } catch (final RuntimeException exception) {
            return completedFailure(detail(exception, "analog-output connection could not be opened"));
        }
    }

    @Override
    public CompletableFuture<Response<DeviceState>> read(final DeviceQuery query) {
        if (!(query instanceof AnalogOutputQuery outputQuery)) {
            return completedFailure("unsupported analog-output query");
        }
        return read(outputQuery).thenApply(result -> result.map(snapshot -> (DeviceState) snapshot));
    }

    public CompletableFuture<Response<AnalogOutputSnapshot>> read(final AnalogOutputQuery query) {
        if (!(query instanceof ReadAnalogOutputs)) {
            return CompletableFuture.completedFuture(Response.failure("unsupported analog-output query"));
        }
        return readOutputs();
    }

    public CompletableFuture<Response<AnalogOutputSnapshot>> readOutputs() {
        final List<PointAddress<?>> points = outputPoints();
        try {
            return normalize(client.read(points), "analog-output read failed")
                    .thenApply(result -> result.flatMap(this::decodeSnapshot));
        } catch (final RuntimeException exception) {
            return completedFailure(detail(exception, "analog-output read failed"));
        }
    }

    @Override
    public CompletableFuture<Response<Void>> execute(final DeviceCommand command) {
        if (command instanceof SetAnalogOutput setOutput) {
            return write(new AnalogOutputSetting(setOutput.channel(), setOutput.value()));
        }
        if (command instanceof SetAnalogOutputs setOutputs) {
            return writeSequentially(setOutputs.settings());
        }
        return completedFailure(command == null
                ? "analog-output command is required" : "unsupported analog-output command");
    }

    @Override
    public CompletableFuture<Response<Void>> close() {
        try {
            return normalize(client.disconnect(), "analog-output connection could not be closed");
        } catch (final RuntimeException exception) {
            return completedFailure(detail(exception, "analog-output connection could not be closed"));
        }
    }

    private static List<PointAddress<?>> outputPoints() {
        final List<PointAddress<?>> points = new ArrayList<>(8);
        for (int channel = 1; channel <= 8; channel++) {
            points.add(outputPoint(new AnalogOutputChannelNumber(channel)));
        }
        return List.copyOf(points);
    }

    private static HoldingRegisterPoint outputPoint(final AnalogOutputChannelNumber channel) {
        return new HoldingRegisterPoint(new StartAddress(channel.value() - 1));
    }

    private Response<AnalogOutputSnapshot> decodeSnapshot(final PointSnapshot snapshot) {
        if (snapshot == null) {
            return Response.failure("analog-output response snapshot is missing");
        }
        final List<AnalogOutputReading> readings = new ArrayList<>(8);
        for (int channel = 1; channel <= 8; channel++) {
            final AnalogOutputChannelNumber channelNumber = new AnalogOutputChannelNumber(channel);
            final Response<Integer> rawValue = snapshot.valueOf(outputPoint(channelNumber));
            if (rawValue.status() != Response.Status.OK) {
                return Response.failure(rawValue.details());
            }
            try {
                readings.add(new AnalogOutputReading(channelNumber, new OutputMillivolts(rawValue.value())));
            } catch (final RuntimeException exception) {
                return Response.failure("channel " + channel + ": "
                        + detail(exception, "output register value is invalid"));
            }
        }
        return Response.success(new AnalogOutputSnapshot(readings));
    }

    private CompletableFuture<Response<Void>> write(final AnalogOutputSetting setting) {
        try {
            return normalize(client.write(outputPoint(setting.channel()), setting.value().value()),
                    "analog-output write failed");
        } catch (final RuntimeException exception) {
            return completedFailure(detail(exception, "analog-output write failed"));
        }
    }

    private CompletableFuture<Response<Void>> writeSequentially(final List<AnalogOutputSetting> settings) {
        CompletableFuture<Response<Void>> result = CompletableFuture.completedFuture(Response.success());
        for (final AnalogOutputSetting setting : settings) {
            result = result.thenCompose(previous -> previous.fold(
                    ignored -> write(setting), failure -> CompletableFuture.completedFuture(
                            Response.failure(failure.details()))));
        }
        return result;
    }

    private static <T> CompletableFuture<Response<T>> normalize(
            final CompletionStage<Response<T>> stage, final String fallback) {
        try {
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
                    return Response.<T>failure(detail(cause, fallback));
                }
                return result == null ? Response.<T>failure(fallback) : result;
            }).toCompletableFuture();
        } catch (final RuntimeException exception) {
            return completedFailure(detail(exception, fallback));
        }
    }

    private static String detail(final Throwable cause, final String fallback) {
        final String message = cause.getMessage();
        return message == null || message.isBlank() ? fallback : message;
    }

    private static <T> CompletableFuture<Response<T>> completedFailure(final String message) {
        return CompletableFuture.completedFuture(Response.failure(message));
    }
}