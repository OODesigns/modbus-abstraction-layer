package com.oodesigns.devicecomms.device.waveshare.relay;

import com.oodesigns.devicecomms.adapter.modbus.CoilPoint;
import com.oodesigns.devicecomms.device.waveshare.relay.command.RelaySetting;
import com.oodesigns.devicecomms.device.waveshare.relay.command.SetRelayState;
import com.oodesigns.devicecomms.device.waveshare.relay.command.SetRelayStates;
import com.oodesigns.devicecomms.device.waveshare.relay.query.ReadRelayStatus;
import com.oodesigns.devicecomms.device.waveshare.relay.query.RelayQuery;
import com.oodesigns.devicecomms.device.waveshare.relay.reading.RelayReading;
import com.oodesigns.devicecomms.device.waveshare.relay.reading.RelaySnapshot;
import com.oodesigns.devicecomms.device.waveshare.relay.value.RelayChannelNumber;
import com.oodesigns.devicecomms.device.waveshare.relay.value.RelayState;
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

public final class Relay30CH implements Device {
    private final CommunicationClient client;

    public Relay30CH(final CommunicationClient client) {
        this.client = Objects.requireNonNull(client, "client");
    }

    @Override
    public CompletableFuture<Response<Void>> open() {
        try {
            return normalize(client.connect(), "relay connection could not be opened");
        } catch (final RuntimeException exception) {
            return completedFailure(detail(exception, "relay connection could not be opened"));
        }
    }

    @Override
    public CompletableFuture<Response<DeviceState>> read(final DeviceQuery query) {
        if (!(query instanceof RelayQuery relayQuery)) {
            return completedFailure(query == null ? "relay query is required" : "unsupported relay query");
        }
        return read(relayQuery).thenApply(result -> result.map(snapshot -> (DeviceState) snapshot));
    }

    public CompletableFuture<Response<RelaySnapshot>> read(final RelayQuery query) {
        if (!(query instanceof ReadRelayStatus)) {
            return completedFailure("unsupported relay query");
        }
        final List<PointAddress<?>> points = relayPoints();
        try {
            return normalize(client.read(points), "relay status read failed")
                    .thenApply(result -> result.flatMap(this::decodeSnapshot));
        } catch (final RuntimeException exception) {
            return completedFailure(detail(exception, "relay status read failed"));
        }
    }

    @Override
    public CompletableFuture<Response<Void>> execute(final DeviceCommand command) {
        if (command instanceof SetRelayState setRelayState) {
            return write(new RelaySetting(setRelayState.channel(), setRelayState.state()));
        }
        if (command instanceof SetRelayStates setRelayStates) {
            return writeSequentially(setRelayStates.settings());
        }
        return completedFailure(command == null ? "relay command is required" : "unsupported relay command");
    }

    @Override
    public CompletableFuture<Response<Void>> close() {
        try {
            return normalize(client.disconnect(), "relay connection could not be closed");
        } catch (final RuntimeException exception) {
            return completedFailure(detail(exception, "relay connection could not be closed"));
        }
    }

    private static List<PointAddress<?>> relayPoints() {
        final List<PointAddress<?>> points = new ArrayList<>(30);
        for (int address = 0; address < 30; address++) {
            points.add(new CoilPoint(new StartAddress(address)));
        }
        return List.copyOf(points);
    }

    private Response<RelaySnapshot> decodeSnapshot(final PointSnapshot pointSnapshot) {
        if (pointSnapshot == null) {
            return Response.failure("relay response snapshot is missing");
        }
        final List<RelayReading> readings = new ArrayList<>(30);
        for (int channel = 1; channel <= 30; channel++) {
            try {
                final RelayChannelNumber channelNumber = new RelayChannelNumber(channel);
                final Response<Boolean> coil = pointSnapshot.valueOf(coilPoint(channelNumber));
                if (coil.status() != Response.Status.OK) {
                    return Response.failure(coil.details());
                }
                if (coil.value() == null) {
                    return Response.failure("relay channel " + channel + " status is missing");
                }
                readings.add(new RelayReading(channelNumber, RelayState.fromCoilState(coil.value())));
            } catch (final RuntimeException exception) {
                return Response.failure("relay channel " + channel + ": "
                        + detail(exception, "relay status value is invalid"));
            }
        }
        return Response.success(new RelaySnapshot(readings));
    }

    private CompletableFuture<Response<Void>> write(final RelaySetting setting) {
        try {
            return normalize(client.write(coilPoint(setting.channel()), setting.state().energized()),
                    "relay coil write failed");
        } catch (final RuntimeException exception) {
            return completedFailure(detail(exception, "relay coil write failed"));
        }
    }

    private CompletableFuture<Response<Void>> writeSequentially(final List<RelaySetting> settings) {
        CompletableFuture<Response<Void>> result = CompletableFuture.completedFuture(Response.success());
        for (final RelaySetting setting : settings) {
            result = result.thenCompose(previous -> previous.fold(ignored -> write(setting)
                .thenApply(writeResult -> writeResult.status() == Response.Status.OK
                    ? writeResult
                    : Response.failure("relay channel " + setting.channel().value()
                        + " write failed: " + writeResult.details())),
                failure -> CompletableFuture.completedFuture(Response.failure(failure.details()))));
        }
        return result;
    }

    private static CoilPoint coilPoint(final RelayChannelNumber channel) {
        return new CoilPoint(new StartAddress(channel.value() - 1));
    }

    private static <T> CompletableFuture<Response<T>> normalize(
            final CompletionStage<Response<T>> stage, final String fallback) {
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
    }

    private static String detail(final Throwable cause, final String fallback) {
        final String message = cause.getMessage();
        return message == null || message.isBlank() ? fallback : message;
    }

    private static <T> CompletableFuture<Response<T>> completedFailure(final String message) {
        return CompletableFuture.completedFuture(Response.failure(message));
    }
}