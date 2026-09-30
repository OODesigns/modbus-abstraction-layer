package com.oodesigns.devicecomms.device.waveshare.relay;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.oodesigns.devicecomms.adapter.modbus.CoilPoint;
import com.oodesigns.devicecomms.device.waveshare.relay.command.RelaySetting;
import com.oodesigns.devicecomms.device.waveshare.relay.command.SetRelayState;
import com.oodesigns.devicecomms.device.waveshare.relay.command.SetRelayStates;
import com.oodesigns.devicecomms.device.waveshare.relay.query.ReadRelayStatus;
import com.oodesigns.devicecomms.device.waveshare.relay.reading.RelaySnapshot;
import com.oodesigns.devicecomms.device.waveshare.relay.value.RelayChannelNumber;
import com.oodesigns.devicecomms.device.waveshare.relay.value.RelayState;
import com.oodesigns.devicecomms.domain.Response;
import com.oodesigns.devicecomms.domain.communication.CommunicationClient;
import com.oodesigns.devicecomms.domain.communication.PointAddress;
import com.oodesigns.devicecomms.domain.communication.PointSnapshot;
import com.oodesigns.devicecomms.domain.device.DeviceQuery;
import com.oodesigns.devicecomms.domain.device.DeviceState;
import com.oodesigns.devicecomms.domain.value.StartAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;

class Relay30CHTest {
    @Test
    void readsStatusOfThirtyRelaysFromCoilsZeroThroughTwentyNine() {
        final RecordingClient client = new RecordingClient();
        client.readValues = relayValues();
        final Relay30CH device = new Relay30CH(client);

        final Response<RelaySnapshot> result = device.read(new ReadRelayStatus()).join();

        assertEquals(Response.Status.OK, result.status());
        assertEquals(30, result.value().relays().size());
        assertEquals(new RelayChannelNumber(1), result.value().relays().getFirst().channel());
        assertEquals(RelayState.ON, result.value().relays().getFirst().state());
        assertEquals(new RelayChannelNumber(30), result.value().relays().getLast().channel());
        assertEquals(RelayState.OFF, result.value().relays().getLast().state());
        assertEquals(30, client.requestedPoints.size());
        assertEquals(new CoilPoint(new StartAddress(29)), client.requestedPoints.getLast());
    }

    @Test
    void providesStatusThroughGenericDeviceQueryContract() {
        final RecordingClient client = new RecordingClient();
        client.readValues = relayValues();

        final Response<DeviceState> result = new Relay30CH(client).read((DeviceQuery) new ReadRelayStatus()).join();

        assertEquals(Response.Status.OK, result.status());
        assertInstanceOf(RelaySnapshot.class, result.value());
    }

    @Test
    void switchesOneRelayOnOrOffUsingItsCoil() {
        final RecordingClient client = new RecordingClient();
        final Relay30CH device = new Relay30CH(client);

        final Response<Void> result = device.execute(
                new SetRelayState(new RelayChannelNumber(4), RelayState.ON)).join();

        assertEquals(Response.Status.OK, result.status());
        assertEquals(List.of(new Write(new CoilPoint(new StartAddress(3)), true)), client.writes);
    }

    @Test
    void appliesBulkRelayStatesSequentiallyAndStopsAfterFailure() {
        final List<RelaySetting> settings = List.of(
                new RelaySetting(new RelayChannelNumber(1), RelayState.ON),
                new RelaySetting(new RelayChannelNumber(2), RelayState.ON),
                new RelaySetting(new RelayChannelNumber(30), RelayState.OFF));
        final RecordingClient client = new RecordingClient();

        final Response<Void> result = new Relay30CH(client).execute(new SetRelayStates(settings)).join();

        assertEquals(Response.Status.OK, result.status());
        assertEquals(List.of(
                new Write(new CoilPoint(new StartAddress(0)), true),
                new Write(new CoilPoint(new StartAddress(1)), true),
                new Write(new CoilPoint(new StartAddress(29)), false)), client.writes);

        final RecordingClient failingClient = new RecordingClient();
        failingClient.failWriteNumber = 2;
        final Response<Void> failed = new Relay30CH(failingClient).execute(new SetRelayStates(settings)).join();
        assertEquals(Response.Status.EXCEPTION, failed.status());
        assertEquals(true, failed.details().contains("relay channel 2"));
        assertEquals(2, failingClient.writes.size());
    }

    @Test
    void rejectsInvalidRelayNumbersAndDuplicateBulkChannels() {
        assertThrows(IllegalArgumentException.class, () -> new RelayChannelNumber(0));
        assertThrows(IllegalArgumentException.class, () -> new RelayChannelNumber(31));
        assertThrows(IllegalArgumentException.class, () -> new SetRelayStates(List.of()));
        assertThrows(IllegalArgumentException.class, () -> new SetRelayStates(List.of(
                new RelaySetting(new RelayChannelNumber(1), RelayState.ON),
                new RelaySetting(new RelayChannelNumber(1), RelayState.OFF))));
    }

    private static Map<PointAddress<?>, Object> relayValues() {
        final Map<PointAddress<?>, Object> values = new HashMap<>();
        for (int address = 0; address < 30; address++) {
            values.put(new CoilPoint(new StartAddress(address)), address == 0 || address == 28);
        }
        return values;
    }

    private record Write(PointAddress<?> point, Object value) {
    }

    private static final class RecordingClient implements CommunicationClient {
        private List<PointAddress<?>> requestedPoints = List.of();
        private Map<PointAddress<?>, Object> readValues = Map.of();
        private final List<Write> writes = new ArrayList<>();
        private int failWriteNumber;

        @Override
        public CompletableFuture<Response<Void>> connect() {
            return CompletableFuture.completedFuture(Response.success());
        }

        @Override
        public CompletableFuture<Response<Void>> disconnect() {
            return CompletableFuture.completedFuture(Response.success());
        }

        @Override
        public Response<Boolean> isConnected() {
            return Response.success(true);
        }

        @Override
        public CompletableFuture<Response<PointSnapshot>> read(final List<PointAddress<?>> points) {
            requestedPoints = List.copyOf(points);
            return CompletableFuture.completedFuture(Response.success(new PointSnapshot(readValues)));
        }

        @Override
        public <T> CompletableFuture<Response<Void>> write(final PointAddress<T> point, final T value) {
            writes.add(new Write(point, value));
            return CompletableFuture.completedFuture(failWriteNumber == writes.size()
                    ? Response.failure("simulated relay write failure") : Response.success());
        }
    }
}
