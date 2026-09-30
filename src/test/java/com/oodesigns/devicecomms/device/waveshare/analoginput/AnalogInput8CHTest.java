package com.oodesigns.devicecomms.device.waveshare.analoginput;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.oodesigns.devicecomms.adapter.modbus.HoldingRegisterPoint;
import com.oodesigns.devicecomms.adapter.modbus.InputRegisterPoint;
import com.oodesigns.devicecomms.device.waveshare.analoginput.profile.AnalogInputProfile;
import com.oodesigns.devicecomms.device.waveshare.analoginput.port.AnalogInputReader;
import com.oodesigns.devicecomms.device.waveshare.analoginput.query.ReadAnalogChannels;
import com.oodesigns.devicecomms.device.waveshare.analoginput.reading.AnalogInputSnapshot;
import com.oodesigns.devicecomms.device.waveshare.analoginput.value.AdcScaleCode;
import com.oodesigns.devicecomms.device.waveshare.analoginput.value.AnalogChannelNumber;
import com.oodesigns.devicecomms.device.waveshare.analoginput.value.CurrentMicroamps;
import com.oodesigns.devicecomms.device.waveshare.analoginput.value.ModuleRevision;
import com.oodesigns.devicecomms.device.waveshare.analoginput.value.VoltageMillivolts;
import com.oodesigns.devicecomms.domain.Response;
import com.oodesigns.devicecomms.domain.communication.CommunicationClient;
import com.oodesigns.devicecomms.domain.communication.PointAddress;
import com.oodesigns.devicecomms.domain.communication.PointSnapshot;
import com.oodesigns.devicecomms.domain.device.DeviceQuery;
import com.oodesigns.devicecomms.domain.device.DeviceState;
import com.oodesigns.devicecomms.domain.value.StartAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;

class AnalogInput8CHTest {
    @Test
    void readsEveryChannelAndReturnsModeSpecificMeasurements() {
        final RecordingCommunicationClient client = new RecordingCommunicationClient();
        client.readResult = Response.success(snapshot(
                new int[] {0, 1, 2, 3, 4, 0, 1, 2},
                new int[] {2500, 3000, 12000, 16000, 2048, 1000, 4000, 20000}));
        final AnalogInput8CH reader = new AnalogInput8CH(client,
            new AnalogInputProfile(ModuleRevision.A));

        final Response<AnalogInputSnapshot> result = reader.readChannels().join();

        assertEquals(Response.Status.OK, result.status());
        assertEquals(8, result.value().channels().size());
        assertEquals(new AnalogChannelNumber(1), result.value().channels().getFirst().channel());
        assertEquals(new AnalogChannelNumber(8), result.value().channels().getLast().channel());
        assertEquals(new VoltageMillivolts(2500),
                result.value().channels().get(0).measurement());
        assertEquals(new VoltageMillivolts(3000),
            result.value().channels().get(1).measurement());
        assertEquals(new CurrentMicroamps(12000),
                result.value().channels().get(2).measurement());
        assertEquals(new CurrentMicroamps(16000),
            result.value().channels().get(3).measurement());
        assertEquals(new AdcScaleCode(2048),
                result.value().channels().get(4).measurement());
        assertEquals(new VoltageMillivolts(1000),
            result.value().channels().get(5).measurement());
        assertEquals(new VoltageMillivolts(4000),
            result.value().channels().get(6).measurement());
        assertEquals(new CurrentMicroamps(20000),
            result.value().channels().get(7).measurement());
        assertEquals(new HoldingRegisterPoint(new StartAddress(0x1000)), client.requestedPoints.get(8));
        assertEquals(new InputRegisterPoint(new StartAddress(0)), client.requestedPoints.get(0));
    }

    @Test
    void modeZeroUsesTheBoardRevisionSpecificVoltageRange() {
        final RecordingCommunicationClient revisionAClient = new RecordingCommunicationClient();
        revisionAClient.readResult = Response.success(snapshot(new int[] {0, 0, 0, 0, 0, 0, 0, 0},
                new int[] {7500, 0, 0, 0, 0, 0, 0, 0}));
        final AnalogInput8CH revisionA = new AnalogInput8CH(
            revisionAClient, new AnalogInputProfile(ModuleRevision.A));

        final Response<AnalogInputSnapshot> revisionAResult = revisionA.readChannels().join();

        assertEquals(Response.Status.EXCEPTION, revisionAResult.status());

        final RecordingCommunicationClient revisionBClient = new RecordingCommunicationClient();
        revisionBClient.readResult = Response.success(snapshot(new int[] {0, 0, 0, 0, 0, 0, 0, 0},
                new int[] {7500, 0, 0, 0, 0, 0, 0, 0}));
        final AnalogInput8CH revisionB = new AnalogInput8CH(
            revisionBClient, new AnalogInputProfile(ModuleRevision.B));

        final Response<AnalogInputSnapshot> revisionBResult = revisionB.readChannels().join();

        assertEquals(Response.Status.OK, revisionBResult.status());
        assertEquals(new VoltageMillivolts(7500), revisionBResult.value().channels().getFirst().measurement());
    }

        @Test
        void offsetVoltageModeUsesRevisionSpecificMinimum() {
        final RecordingCommunicationClient revisionAClient = new RecordingCommunicationClient();
        revisionAClient.readResult = Response.success(snapshot(new int[] {1, 0, 0, 0, 0, 0, 0, 0},
            new int[] {1500, 0, 0, 0, 0, 0, 0, 0}));
        final Response<AnalogInputSnapshot> revisionAResult = new AnalogInput8CH(
            revisionAClient, new AnalogInputProfile(ModuleRevision.A)).readChannels().join();
        assertEquals(Response.Status.OK, revisionAResult.status());

        final RecordingCommunicationClient revisionBClient = new RecordingCommunicationClient();
        revisionBClient.readResult = Response.success(snapshot(new int[] {1, 0, 0, 0, 0, 0, 0, 0},
            new int[] {1500, 0, 0, 0, 0, 0, 0, 0}));
        final Response<AnalogInputSnapshot> revisionBResult = new AnalogInput8CH(
            revisionBClient, new AnalogInputProfile(ModuleRevision.B)).readChannels().join();
        assertEquals(Response.Status.EXCEPTION, revisionBResult.status());
        }

        @Test
        void fourToTwentyMilliampModeRejectsValuesBelowFourMilliamps() {
        final RecordingCommunicationClient client = new RecordingCommunicationClient();
        client.readResult = Response.success(snapshot(new int[] {3, 0, 0, 0, 0, 0, 0, 0},
            new int[] {3999, 0, 0, 0, 0, 0, 0, 0}));

        final Response<AnalogInputSnapshot> result = new AnalogInput8CH(
            client, new AnalogInputProfile(ModuleRevision.A)).readChannels().join();

        assertEquals(Response.Status.EXCEPTION, result.status());
        }

    @Test
    void unsupportedChannelModeReturnsFailureInsteadOfThrowing() {
        final RecordingCommunicationClient client = new RecordingCommunicationClient();
        client.readResult = Response.success(snapshot(new int[] {5, 0, 0, 0, 0, 0, 0, 0},
                new int[] {0, 0, 0, 0, 0, 0, 0, 0}));
        final AnalogInput8CH reader = new AnalogInput8CH(client,
            new AnalogInputProfile(ModuleRevision.A));

        final Response<AnalogInputSnapshot> result = reader.readChannels().join();

        assertEquals(Response.Status.EXCEPTION, result.status());
    }

    @Test
    void scaleCodeUsesThe12BitMaximumFromTheConversionFormula() {
        assertEquals(new AdcScaleCode(4095), new AdcScaleCode(4095));
        assertThrows(IllegalArgumentException.class, () -> new AdcScaleCode(4096));
    }

    @Test
    void isComposableThroughGenericDeviceReadContract() {
        final RecordingCommunicationClient client = new RecordingCommunicationClient();
        client.readResult = Response.success(snapshot(new int[] {2, 2, 2, 2, 2, 2, 2, 2},
                new int[] {100, 200, 300, 400, 500, 600, 700, 800}));
        final AnalogInput8CH reader = new AnalogInput8CH(client,
            new AnalogInputProfile(ModuleRevision.A));
        final DeviceQuery query = new ReadAnalogChannels();

        final Response<DeviceState> result = reader.read(query).join();

        assertEquals(Response.Status.OK, result.status());
        assertInstanceOf(AnalogInputSnapshot.class, result.value());
    }

    @Test
    void exposesTypedChannelsThroughComposableReaderPort() {
        final RecordingCommunicationClient client = new RecordingCommunicationClient();
        client.readResult = Response.success(snapshot(new int[] {2, 2, 2, 2, 2, 2, 2, 2},
                new int[] {10, 20, 30, 40, 50, 60, 70, 80}));
        final AnalogInputReader component = new AnalogInput8CH(
                client, new AnalogInputProfile(ModuleRevision.A));

        final Response<AnalogInputSnapshot> result = component.readChannels().join();

        assertEquals(Response.Status.OK, result.status());
        assertEquals(8, result.value().channels().size());
    }

    private static PointSnapshot snapshot(final int[] modes, final int[] readings) {
        final Map<PointAddress<?>, Object> values = new HashMap<>();
        for (int channelIndex = 0; channelIndex < 8; channelIndex++) {
            values.put(new InputRegisterPoint(new StartAddress(channelIndex)), readings[channelIndex]);
            values.put(new HoldingRegisterPoint(new StartAddress(0x1000 + channelIndex)), modes[channelIndex]);
        }
        return new PointSnapshot(values);
    }

    private static final class RecordingCommunicationClient implements CommunicationClient {
        private Response<PointSnapshot> readResult = Response.failure("no read result configured");
        private List<PointAddress<?>> requestedPoints = List.of();

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
            return CompletableFuture.completedFuture(readResult);
        }

        @Override
        public <T> CompletableFuture<Response<Void>> write(final PointAddress<T> point, final T value) {
            return CompletableFuture.completedFuture(Response.failure("sensor reader is read-only"));
        }
    }
}