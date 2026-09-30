package com.oodesigns.devicecomms.device.blauberg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.oodesigns.devicecomms.adapter.modbus.CoilPoint;
import com.oodesigns.devicecomms.adapter.modbus.HoldingRegisterPoint;
import com.oodesigns.devicecomms.adapter.modbus.InputRegisterPoint;
import com.oodesigns.devicecomms.device.blauberg.command.PresetAirflowSettings;
import com.oodesigns.devicecomms.device.blauberg.command.Sensor;
import com.oodesigns.devicecomms.device.blauberg.command.SetControllerPower;
import com.oodesigns.devicecomms.device.blauberg.command.SetFanMode;
import com.oodesigns.devicecomms.device.blauberg.command.SetManualFanSpeed;
import com.oodesigns.devicecomms.device.blauberg.command.SetPresetAirflow;
import com.oodesigns.devicecomms.device.blauberg.command.SetSensorEnabled;
import com.oodesigns.devicecomms.device.blauberg.command.SetUnitOnOff;
import com.oodesigns.devicecomms.device.blauberg.command.UpdateWeeklyTemperature;
import com.oodesigns.devicecomms.device.blauberg.query.ReadSnapshot;
import com.oodesigns.devicecomms.device.blauberg.reading.MvhrSnapshot;
import com.oodesigns.devicecomms.device.blauberg.value.AirflowRate;
import com.oodesigns.devicecomms.device.blauberg.value.FanMode;
import com.oodesigns.devicecomms.device.blauberg.value.FanPreset;
import com.oodesigns.devicecomms.device.blauberg.value.FanSpeedPercent;
import com.oodesigns.devicecomms.device.blauberg.value.ScheduleDay;
import com.oodesigns.devicecomms.device.blauberg.value.SchedulePeriodNumber;
import com.oodesigns.devicecomms.device.blauberg.value.ScheduleSlot;
import com.oodesigns.devicecomms.device.blauberg.value.ScheduledFanSpeed;
import com.oodesigns.devicecomms.device.blauberg.value.WeeklyTemperatureMode;
import com.oodesigns.devicecomms.device.blauberg.value.WeeklyTemperatureSetpointCelsius;
import com.oodesigns.devicecomms.domain.Response;
import com.oodesigns.devicecomms.domain.communication.CommunicationClient;
import com.oodesigns.devicecomms.domain.communication.PointAddress;
import com.oodesigns.devicecomms.domain.communication.PointSnapshot;
import com.oodesigns.devicecomms.domain.device.DeviceQuery;
import com.oodesigns.devicecomms.domain.device.DeviceState;
import com.oodesigns.devicecomms.domain.value.StartAddress;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;

class BlaubergMVHRTest {
    @Test
    void readSnapshotRequestsTheKeyS21PointsAndReturnsTypedModes() {
        final RecordingCommunicationClient client = new RecordingCommunicationClient();
        client.readResult = Response.success(new PointSnapshot(validSnapshotValues()));
        final BlaubergMVHR device = new BlaubergMVHR(client);

        final Response<MvhrSnapshot> result = device.read(new ReadSnapshot()).join();

        assertEquals(Response.Status.OK, result.status());
        assertEquals(List.of(
                new CoilPoint(new StartAddress(0)),
                new CoilPoint(new StartAddress(1)),
                new CoilPoint(new StartAddress(2)),
                new HoldingRegisterPoint(new StartAddress(2)),
                new InputRegisterPoint(new StartAddress(0)),
                new InputRegisterPoint(new StartAddress(2)),
                new InputRegisterPoint(new StartAddress(5)),
                new InputRegisterPoint(new StartAddress(10)),
                new InputRegisterPoint(new StartAddress(12)),
                new InputRegisterPoint(new StartAddress(14)),
                new InputRegisterPoint(new StartAddress(16)),
                new InputRegisterPoint(new StartAddress(19)),
                new InputRegisterPoint(new StartAddress(20)),
                new InputRegisterPoint(new StartAddress(21)),
                new InputRegisterPoint(new StartAddress(22)),
                new InputRegisterPoint(new StartAddress(23)),
                new InputRegisterPoint(new StartAddress(24)),
                new InputRegisterPoint(new StartAddress(31)),
                new InputRegisterPoint(new StartAddress(32)),
                new InputRegisterPoint(new StartAddress(33)),
                new InputRegisterPoint(new StartAddress(48))), client.requestedPoints);
        assertEquals(true, result.value().operation().controllerPower());
        assertEquals(true, result.value().operation().unitOn());
        assertEquals(false, result.value().operation().weeklyTimerEnabled());
        assertEquals(FanMode.MANUAL, result.value().fan().mode());
        assertEquals(ScheduledFanSpeed.STANDBY, result.value().fan().weeklySpeed());
        assertEquals(1820, result.value().fan().supplyRpm().value());
        assertEquals(1760, result.value().fan().extractRpm().value());
        assertEquals(1, result.value().maintenance().filterCondition().value());
        assertEquals(WeeklyTemperatureMode.VENTILATION_ONLY,
                result.value().climate().activeWeeklyTemperature().mode());
    }

    @Test
    void readSnapshotReturnsFailureForUnsupportedDeviceModeValue() {
        final RecordingCommunicationClient client = new RecordingCommunicationClient();
        final Map<PointAddress<?>, Object> values = validSnapshotValues();
        seed(values, new HoldingRegisterPoint(new StartAddress(2)), 999);
        client.readResult = Response.success(new PointSnapshot(values));
        final BlaubergMVHR device = new BlaubergMVHR(client);

        final Response<MvhrSnapshot> result = device.read(new ReadSnapshot()).join();

        assertEquals(Response.Status.EXCEPTION, result.status());
    }

    @Test
    void snapshotIsAvailableThroughTheGenericDeviceQueryContract() {
        final RecordingCommunicationClient client = new RecordingCommunicationClient();
        client.readResult = Response.success(new PointSnapshot(validSnapshotValues()));
        final BlaubergMVHR device = new BlaubergMVHR(client);
        final DeviceQuery query = new ReadSnapshot();

        final Response<DeviceState> result = device.read(query).join();

        assertEquals(Response.Status.OK, result.status());
        assertInstanceOf(MvhrSnapshot.class, result.value());
    }
    @Test
    void readSnapshotDecodesTypedTelemetryAndSensorAbsence() {
        final RecordingCommunicationClient client = new RecordingCommunicationClient();
        final Map<PointAddress<?>, Object> values = validSnapshotValues();
        seed(values, new InputRegisterPoint(new StartAddress(0)), 215);
        seed(values, new InputRegisterPoint(new StartAddress(2)), 250);
        seed(values, new InputRegisterPoint(new StartAddress(5)), 0x8000);
        seed(values, new InputRegisterPoint(new StartAddress(10)), 0);
        seed(values, new InputRegisterPoint(new StartAddress(12)), 850);
        seed(values, new InputRegisterPoint(new StartAddress(14)), 18);
        seed(values, new InputRegisterPoint(new StartAddress(16)), 35);
        seed(values, new InputRegisterPoint(new StartAddress(19)), 480);
        seed(values, new InputRegisterPoint(new StartAddress(20)), 470);
        seed(values, new InputRegisterPoint(new StartAddress(21)), 120);
        seed(values, new InputRegisterPoint(new StartAddress(22)), 110);
        seed(values, new InputRegisterPoint(new StartAddress(48)), 250);
        client.readResult = Response.success(new PointSnapshot(values));
        final BlaubergMVHR device = new BlaubergMVHR(client);

        final Response<MvhrSnapshot> result = device.read(new ReadSnapshot()).join();

        assertEquals(Response.Status.OK, result.status());
        assertEquals(21.5, result.value().climate().selectedTemperature().orElseThrow().value());
        assertEquals(25.0, result.value().climate().supplyAirTemperature().orElseThrow().value());
        assertTrue(result.value().climate().outdoorTemperature().isEmpty());
        assertTrue(result.value().airQuality().internalHumidity().isEmpty());
        assertEquals(850, result.value().airQuality().internalCarbonDioxide().orElseThrow().value());
        assertEquals(18, result.value().airQuality().internalPm25().orElseThrow().value());
        assertEquals(35, result.value().airQuality().internalVoc().orElseThrow().value());
        assertEquals(480, result.value().fan().supplyAirflow().value());
        assertEquals(470, result.value().fan().extractAirflow().value());
        assertEquals(120, result.value().fan().supplyPressure().value());
        assertEquals(110, result.value().fan().extractPressure().value());
        assertEquals(25.0, result.value().climate().calculatedSupplyAirTarget().orElseThrow().value());
    }

    @Test
    void readSnapshotRejectsSupplyTargetOutsideTheDocumentedRawRange() {
        final RecordingCommunicationClient client = new RecordingCommunicationClient();
        final Map<PointAddress<?>, Object> values = validSnapshotValues();
        seed(values, new InputRegisterPoint(new StartAddress(48)), 0);
        client.readResult = Response.success(new PointSnapshot(values));
        final BlaubergMVHR device = new BlaubergMVHR(client);

        final Response<MvhrSnapshot> result = device.read(new ReadSnapshot()).join();

        assertEquals(Response.Status.EXCEPTION, result.status());
    }

    @Test
    void controllerPowerUsesItsOwnCoilAndIsSeparateFromUnitOnOff() {
        final RecordingCommunicationClient client = new RecordingCommunicationClient();
        final BlaubergMVHR device = new BlaubergMVHR(client);

        final Response<Void> result = device.execute(new SetControllerPower(false)).join();

        assertEquals(Response.Status.OK, result.status());
        assertEquals(new CoilPoint(new StartAddress(0)), client.writes.getFirst().point());
        assertEquals(false, client.writes.getFirst().value());
    }

    @Test
    void setUnitOnOffWritesTheDocumentedUnitOnOffCoil() {
        final RecordingCommunicationClient client = new RecordingCommunicationClient();
        final BlaubergMVHR device = new BlaubergMVHR(client);

        final Response<Void> result = device.execute(new SetUnitOnOff(false)).join();

        assertEquals(Response.Status.OK, result.status());
        assertEquals(new CoilPoint(new StartAddress(1)), client.writes.getFirst().point());
        assertEquals(false, client.writes.getFirst().value());
    }

    @Test
    void setFanModeWritesThePresetCodeToSpeedModeRegister() {
        final RecordingCommunicationClient client = new RecordingCommunicationClient();
        final BlaubergMVHR device = new BlaubergMVHR(client);

        final Response<Void> result = device.execute(new SetFanMode(FanMode.SPEED_3)).join();

        assertEquals(Response.Status.OK, result.status());
        assertEquals(new HoldingRegisterPoint(new StartAddress(2)), client.writes.getFirst().point());
        assertEquals(3, client.writes.getFirst().value());
    }

    @Test
    void sensorEnableCommandUsesTheConfiguredSensorCoil() {
        final RecordingCommunicationClient client = new RecordingCommunicationClient();
        final BlaubergMVHR device = new BlaubergMVHR(client);

        final Response<Void> result = device.execute(
                new SetSensorEnabled(Sensor.INTERNAL_CO2, true)).join();

        assertEquals(Response.Status.OK, result.status());
        assertEquals(new CoilPoint(new StartAddress(7)), client.writes.getFirst().point());
        assertEquals(true, client.writes.getFirst().value());
    }

    @Test
    void presetAirflowWritesSupplyAndExtractValuesToThePresetPair() {
        final RecordingCommunicationClient client = new RecordingCommunicationClient();
        final BlaubergMVHR device = new BlaubergMVHR(client);
        final PresetAirflowSettings settings = new PresetAirflowSettings(
                FanPreset.SPEED_2, new AirflowRate(450), new AirflowRate(420));

        final Response<Void> result = device.execute(new SetPresetAirflow(settings)).join();

        assertEquals(Response.Status.OK, result.status());
        assertEquals(List.of(
                new Write(new HoldingRegisterPoint(new StartAddress(29)), 450),
                new Write(new HoldingRegisterPoint(new StartAddress(30)), 420)), client.writes);
    }

    @Test
    void presetAirflowDoesNotWriteExtractValueAfterSupplyWriteFailure() {
        final RecordingCommunicationClient client = new RecordingCommunicationClient();
        client.writeResults.addLast(Response.failure("supply airflow write failed"));
        final BlaubergMVHR device = new BlaubergMVHR(client);
        final PresetAirflowSettings settings = new PresetAirflowSettings(
                FanPreset.SPEED_2, new AirflowRate(450), new AirflowRate(420));

        final Response<Void> result = device.execute(new SetPresetAirflow(settings)).join();

        assertEquals(Response.Status.EXCEPTION, result.status());
        assertEquals("supply airflow write failed", result.details());
        assertEquals(1, client.writes.size());
    }

    @Test
    void airflowRateRejectsValuesOutsideTheS21Range() {
        assertThrows(IllegalArgumentException.class, () -> new AirflowRate(-1));
        assertThrows(IllegalArgumentException.class, () -> new AirflowRate(10001));
    }

    @Test
    void manualSpeedSelectsManualModeBeforeWritingTheValidatedPercentage() {
        final RecordingCommunicationClient client = new RecordingCommunicationClient();
        final BlaubergMVHR device = new BlaubergMVHR(client);

        final Response<Void> result = device.execute(
                new SetManualFanSpeed(new FanSpeedPercent(55))).join();

        assertEquals(Response.Status.OK, result.status());
        assertEquals(List.of(
                new Write(new HoldingRegisterPoint(new StartAddress(2)), 255),
                new Write(new HoldingRegisterPoint(new StartAddress(17)), 55)), client.writes);
    }

    @Test
    void manualSpeedDoesNotWritePercentageIfManualModeSelectionFails() {
        final RecordingCommunicationClient client = new RecordingCommunicationClient();
        client.writeResults.addLast(Response.failure("mode register unavailable"));
        final BlaubergMVHR device = new BlaubergMVHR(client);

        final Response<Void> result = device.execute(
                new SetManualFanSpeed(new FanSpeedPercent(55))).join();

        assertEquals(Response.Status.EXCEPTION, result.status());
        assertEquals("mode register unavailable", result.details());
        assertEquals(1, client.writes.size());
    }

    @Test
    void fanSpeedPercentageRejectsValuesOutsideTheS21Range() {
        assertThrows(IllegalArgumentException.class, () -> new FanSpeedPercent(-1));
        assertThrows(IllegalArgumentException.class, () -> new FanSpeedPercent(101));
    }

    @Test
    void weeklySetpointRejectsValuesOutsideTheS21ScheduleRange() {
        assertThrows(IllegalArgumentException.class, () -> new WeeklyTemperatureSetpointCelsius(1000));
        assertThrows(IllegalArgumentException.class, () -> new WeeklyTemperatureSetpointCelsius(14.9));
        assertThrows(IllegalArgumentException.class, () -> new WeeklyTemperatureSetpointCelsius(21.5));
    }

    @Test
    void weeklyTemperatureUpdatePreservesTheExistingSpeedByte() {
        final RecordingCommunicationClient client = new RecordingCommunicationClient();
        final HoldingRegisterPoint scheduleWord = new HoldingRegisterPoint(new StartAddress(126));
        final Map<PointAddress<?>, Object> currentValues = new HashMap<>();
        seed(currentValues, scheduleWord, 0x0217);
        client.readResult = Response.success(new PointSnapshot(currentValues));
        final BlaubergMVHR device = new BlaubergMVHR(client);

        final Response<Void> result = device.execute(new UpdateWeeklyTemperature(
                new ScheduleSlot(ScheduleDay.MONDAY, new SchedulePeriodNumber(1)),
                new WeeklyTemperatureSetpointCelsius(25))).join();

        assertEquals(Response.Status.OK, result.status());
        assertEquals(List.of(scheduleWord), client.requestedPoints);
        assertEquals(List.of(new Write(scheduleWord, 0x0219)), client.writes);
    }

    @Test
    void weeklyTemperatureUpdateMapsDayAndPeriodToItsDocumentedRegister() {
        final RecordingCommunicationClient client = new RecordingCommunicationClient();
        final HoldingRegisterPoint scheduleWord = new HoldingRegisterPoint(new StartAddress(138));
        final Map<PointAddress<?>, Object> currentValues = new HashMap<>();
        seed(currentValues, scheduleWord, 0x0317);
        client.readResult = Response.success(new PointSnapshot(currentValues));
        final BlaubergMVHR device = new BlaubergMVHR(client);

        final Response<Void> result = device.execute(new UpdateWeeklyTemperature(
                new ScheduleSlot(ScheduleDay.TUESDAY, new SchedulePeriodNumber(3)),
                new WeeklyTemperatureSetpointCelsius(24))).join();

        assertEquals(Response.Status.OK, result.status());
        assertEquals(List.of(new Write(scheduleWord, 0x0318)), client.writes);
    }

    @Test
    void weeklyTemperatureUpdateDoesNotWriteWhenCurrentScheduleWordCannotBeRead() {
        final RecordingCommunicationClient client = new RecordingCommunicationClient();
        client.readResult = Response.failure("schedule word unavailable");
        final BlaubergMVHR device = new BlaubergMVHR(client);

        final Response<Void> result = device.execute(new UpdateWeeklyTemperature(
                new ScheduleSlot(ScheduleDay.MONDAY, new SchedulePeriodNumber(1)),
                new WeeklyTemperatureSetpointCelsius(25))).join();

        assertEquals(Response.Status.EXCEPTION, result.status());
        assertEquals("schedule word unavailable", result.details());
        assertEquals(List.of(), client.writes);
    }

    @Test
    void weeklyTemperatureUpdateDoesNotWriteWhenExistingFanPresetIsInvalid() {
        final RecordingCommunicationClient client = new RecordingCommunicationClient();
        final HoldingRegisterPoint scheduleWord = new HoldingRegisterPoint(new StartAddress(126));
        final Map<PointAddress<?>, Object> currentValues = new HashMap<>();
        seed(currentValues, scheduleWord, 0x0917);
        client.readResult = Response.success(new PointSnapshot(currentValues));
        final BlaubergMVHR device = new BlaubergMVHR(client);

        final Response<Void> result = device.execute(new UpdateWeeklyTemperature(
                new ScheduleSlot(ScheduleDay.MONDAY, new SchedulePeriodNumber(1)),
                new WeeklyTemperatureSetpointCelsius(25))).join();

        assertEquals(Response.Status.EXCEPTION, result.status());
        assertEquals(List.of(), client.writes);
    }

    private static <T> void seed(final Map<PointAddress<?>, Object> values,
                                 final PointAddress<T> point,
                                 final T value) {
        values.put(point, value);
    }

    private static Map<PointAddress<?>, Object> validSnapshotValues() {
        final Map<PointAddress<?>, Object> values = new HashMap<>();
        seed(values, new CoilPoint(new StartAddress(0)), true);
        seed(values, new CoilPoint(new StartAddress(1)), true);
        seed(values, new CoilPoint(new StartAddress(2)), false);
        seed(values, new HoldingRegisterPoint(new StartAddress(2)), 255);
        seed(values, new InputRegisterPoint(new StartAddress(23)), 1820);
        seed(values, new InputRegisterPoint(new StartAddress(24)), 1760);
        seed(values, new InputRegisterPoint(new StartAddress(31)), 1);
        seed(values, new InputRegisterPoint(new StartAddress(32)), 0);
        seed(values, new InputRegisterPoint(new StartAddress(33)), 0);
        for (final int address : List.of(0, 2, 5, 10, 12, 14, 16, 19, 20, 21, 22, 48)) {
            final int value = address == 48 ? 250 : 0;
            values.putIfAbsent(new InputRegisterPoint(new StartAddress(address)), value);
        }
        return values;
    }

    private record Write(PointAddress<?> point, Object value) {
    }

    private static final class RecordingCommunicationClient implements CommunicationClient {
        private final List<Write> writes = new ArrayList<>();
        private final Deque<Response<Void>> writeResults = new ArrayDeque<>();
        private List<PointAddress<?>> requestedPoints = List.of();
        private Response<PointSnapshot> readResult = Response.failure("no read result configured");

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
            writes.add(new Write(point, value));
            return CompletableFuture.completedFuture(writeResults.isEmpty()
                    ? Response.success()
                    : writeResults.removeFirst());
        }
    }
}