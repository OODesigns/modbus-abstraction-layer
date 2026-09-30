package com.oodesigns.devicecomms.device.waveshare.analogoutput;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.oodesigns.devicecomms.adapter.modbus.HoldingRegisterPoint;
import com.oodesigns.devicecomms.device.waveshare.analogoutput.command.AnalogOutputSetting;
import com.oodesigns.devicecomms.device.waveshare.analogoutput.command.SetAnalogOutput;
import com.oodesigns.devicecomms.device.waveshare.analogoutput.command.SetAnalogOutputs;
import com.oodesigns.devicecomms.device.waveshare.analogoutput.query.ReadAnalogOutputs;
import com.oodesigns.devicecomms.device.waveshare.analogoutput.reading.AnalogOutputSnapshot;
import com.oodesigns.devicecomms.device.waveshare.analogoutput.value.AnalogOutputChannelNumber;
import com.oodesigns.devicecomms.device.waveshare.analogoutput.value.OutputMillivolts;
import com.oodesigns.devicecomms.domain.Response;
import com.oodesigns.devicecomms.domain.communication.CommunicationClient;
import com.oodesigns.devicecomms.domain.communication.PointAddress;
import com.oodesigns.devicecomms.domain.communication.PointSnapshot;
import com.oodesigns.devicecomms.domain.device.DeviceCommand;
import com.oodesigns.devicecomms.domain.device.DeviceQuery;
import com.oodesigns.devicecomms.domain.device.DeviceState;
import com.oodesigns.devicecomms.domain.value.StartAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;

class AnalogOutput8CHTest {
    @Test
    void readsOutputRegistersAsTypedChannelValues() {
        final RecordingCommunicationClient client = new RecordingCommunicationClient();
        client.readValues = outputValues(1000, 2000, 3000, 4000, 5000, 6000, 7000, 10000);
        final AnalogOutput8CH device = new AnalogOutput8CH(client);

        final Response<AnalogOutputSnapshot> result = device.read(new ReadAnalogOutputs()).join();

        assertEquals(Response.Status.OK, result.status());
        assertEquals(8, result.value().channels().size());
        assertEquals(new AnalogOutputChannelNumber(1), result.value().channels().getFirst().channel());
        assertEquals(new OutputMillivolts(1000), result.value().channels().getFirst().value());
        assertEquals(new OutputMillivolts(10000), result.value().channels().getLast().value());
        assertEquals(8, client.requestedPoints.size());
        assertEquals(new HoldingRegisterPoint(new StartAddress(7)), client.requestedPoints.getLast());
    }

    @Test
    void readsOutputSnapshotThroughGenericDeviceQueryContract() {
        final RecordingCommunicationClient client = new RecordingCommunicationClient();
        client.readValues = outputValues(0, 0, 0, 0, 0, 0, 0, 0);
        final AnalogOutput8CH device = new AnalogOutput8CH(client);
        final DeviceQuery query = new ReadAnalogOutputs();

        final Response<DeviceState> result = device.read(query).join();

        assertEquals(Response.Status.OK, result.status());
        assertInstanceOf(AnalogOutputSnapshot.class, result.value());
    }

    @Test
    void returnsFailureForARegisterValueWithTheWrongRuntimeType() {
        final RecordingCommunicationClient client = new RecordingCommunicationClient();
        final Map<PointAddress<?>, Object> malformedValues = new HashMap<>(outputValues(0, 0, 0, 0, 0, 0, 0, 0));
        malformedValues.put(new HoldingRegisterPoint(new StartAddress(0)), "1000");
        client.readValues = malformedValues;

        final Response<AnalogOutputSnapshot> result = new AnalogOutput8CH(client).readOutputs().join();

        assertEquals(Response.Status.EXCEPTION, result.status());
    }

    @Test
    void setsOneChannelUsingItsHoldingRegister() {
        final RecordingCommunicationClient client = new RecordingCommunicationClient();
        final AnalogOutput8CH device = new AnalogOutput8CH(client);
        final DeviceCommand command = new SetAnalogOutput(
                new AnalogOutputChannelNumber(3), new OutputMillivolts(5000));

        final Response<Void> result = device.execute(command).join();

        assertEquals(Response.Status.OK, result.status());
        assertEquals(List.of(new Write(new HoldingRegisterPoint(new StartAddress(2)), 5000)), client.writes);
    }

    @Test
    void setsMultipleChannelsInOrderAndStopsOnFirstFailure() {
        final RecordingCommunicationClient client = new RecordingCommunicationClient();
        final AnalogOutput8CH device = new AnalogOutput8CH(client);
        final SetAnalogOutputs command = new SetAnalogOutputs(List.of(
                new AnalogOutputSetting(new AnalogOutputChannelNumber(2), new OutputMillivolts(2000)),
                new AnalogOutputSetting(new AnalogOutputChannelNumber(5), new OutputMillivolts(7500))));

        final Response<Void> result = device.execute(command).join();

        assertEquals(Response.Status.OK, result.status());
        assertEquals(List.of(
                new Write(new HoldingRegisterPoint(new StartAddress(1)), 2000),
                new Write(new HoldingRegisterPoint(new StartAddress(4)), 7500)), client.writes);

        final RecordingCommunicationClient failingClient = new RecordingCommunicationClient();
        failingClient.failWriteNumber = 2;
        final Response<Void> failed = new AnalogOutput8CH(failingClient).execute(command).join();
        assertEquals(Response.Status.EXCEPTION, failed.status());
        assertEquals(2, failingClient.writes.size());
    }

    @Test
    void validatesChannelNumbersOutputBoundsAndUniqueMultiWriteChannels() {
        assertThrows(IllegalArgumentException.class, () -> new AnalogOutputChannelNumber(0));
        assertThrows(IllegalArgumentException.class, () -> new AnalogOutputChannelNumber(9));
        assertThrows(IllegalArgumentException.class, () -> new OutputMillivolts(-1));
        assertThrows(IllegalArgumentException.class, () -> new OutputMillivolts(10001));
        assertThrows(IllegalArgumentException.class, () -> new SetAnalogOutputs(List.of()));
        assertThrows(IllegalArgumentException.class, () -> new SetAnalogOutputs(List.of(
                new AnalogOutputSetting(new AnalogOutputChannelNumber(1), new OutputMillivolts(0)),
                new AnalogOutputSetting(new AnalogOutputChannelNumber(1), new OutputMillivolts(5000)))));
    }

    @Test
    void connectionLifecycleReturnsFailuresWhenClientThrowsSynchronously() {
        final RecordingCommunicationClient openingClient = new RecordingCommunicationClient();
        openingClient.throwOnConnect = true;
        final Response<Void> openResult = new AnalogOutput8CH(openingClient).open().join();
        assertEquals(Response.Status.EXCEPTION, openResult.status());

        final RecordingCommunicationClient closingClient = new RecordingCommunicationClient();
        closingClient.throwOnDisconnect = true;
        final Response<Void> closeResult = new AnalogOutput8CH(closingClient).close().join();
        assertEquals(Response.Status.EXCEPTION, closeResult.status());
    }

    private static Map<PointAddress<?>, Object> outputValues(final int... values) {
        final Map<PointAddress<?>, Object> outputValues = new HashMap<>();
        for (int channelIndex = 0; channelIndex < values.length; channelIndex++) {
            outputValues.put(new HoldingRegisterPoint(new StartAddress(channelIndex)), values[channelIndex]);
        }
        return outputValues;
    }

    private record Write(PointAddress<?> point, Object value) {
    }

    private static final class RecordingCommunicationClient implements CommunicationClient {
        private final List<Write> writes = new ArrayList<>();
        private List<PointAddress<?>> requestedPoints = List.of();
        private Map<PointAddress<?>, Object> readValues = Map.of();
        private int failWriteNumber;
        private boolean throwOnConnect;
        private boolean throwOnDisconnect;

        @Override
        public CompletableFuture<Response<Void>> connect() {
            if (throwOnConnect) {
                throw new IllegalStateException("simulated connect failure");
            }
            return CompletableFuture.completedFuture(Response.success());
        }

        @Override
        public CompletableFuture<Response<Void>> disconnect() {
            if (throwOnDisconnect) {
                throw new IllegalStateException("simulated disconnect failure");
            }
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
            if (failWriteNumber > 0 && writes.size() == failWriteNumber) {
                return CompletableFuture.completedFuture(Response.failure("simulated write failure"));
            }
            return CompletableFuture.completedFuture(Response.success());
        }
    }
}