package com.oodesigns.devicecomms.device.blauberg;

import com.oodesigns.devicecomms.adapter.modbus.CoilPoint;
import com.oodesigns.devicecomms.adapter.modbus.HoldingRegisterPoint;
import com.oodesigns.devicecomms.adapter.modbus.InputRegisterPoint;
import com.oodesigns.devicecomms.device.blauberg.command.PresetAirflowSettings;
import com.oodesigns.devicecomms.device.blauberg.command.SetControllerPower;
import com.oodesigns.devicecomms.device.blauberg.command.SetFanMode;
import com.oodesigns.devicecomms.device.blauberg.command.SetManualFanSpeed;
import com.oodesigns.devicecomms.device.blauberg.command.SetPresetAirflow;
import com.oodesigns.devicecomms.device.blauberg.command.SetSensorEnabled;
import com.oodesigns.devicecomms.device.blauberg.command.SetUnitOnOff;
import com.oodesigns.devicecomms.device.blauberg.command.UpdateWeeklyTemperature;
import com.oodesigns.devicecomms.device.blauberg.protocol.S21RegisterValue;
import com.oodesigns.devicecomms.device.blauberg.query.MvhrQuery;
import com.oodesigns.devicecomms.device.blauberg.query.ReadSnapshot;
import com.oodesigns.devicecomms.device.blauberg.reading.ActiveWeeklyTemperature;
import com.oodesigns.devicecomms.device.blauberg.reading.AirQualitySettings;
import com.oodesigns.devicecomms.device.blauberg.reading.ClimateSettings;
import com.oodesigns.devicecomms.device.blauberg.reading.FanSettings;
import com.oodesigns.devicecomms.device.blauberg.reading.MaintenanceSettings;
import com.oodesigns.devicecomms.device.blauberg.reading.MvhrSnapshot;
import com.oodesigns.devicecomms.device.blauberg.reading.OperationSettings;
import com.oodesigns.devicecomms.device.blauberg.value.AirflowRate;
import com.oodesigns.devicecomms.device.blauberg.value.CarbonDioxidePpm;
import com.oodesigns.devicecomms.device.blauberg.value.DuctPressurePascal;
import com.oodesigns.devicecomms.device.blauberg.value.FanMode;
import com.oodesigns.devicecomms.device.blauberg.value.FanRpm;
import com.oodesigns.devicecomms.device.blauberg.value.FilterCondition;
import com.oodesigns.devicecomms.device.blauberg.value.MvhrTemperatureCelsius;
import com.oodesigns.devicecomms.device.blauberg.value.Pm25Concentration;
import com.oodesigns.devicecomms.device.blauberg.value.RelativeHumidityPercent;
import com.oodesigns.devicecomms.device.blauberg.value.ScheduledFanSpeed;
import com.oodesigns.devicecomms.device.blauberg.value.SupplyAirTargetCelsius;
import com.oodesigns.devicecomms.device.blauberg.value.VocPercent;
import com.oodesigns.devicecomms.device.blauberg.value.WeeklyTemperatureSetpointCelsius;
import com.oodesigns.devicecomms.domain.Response;
import com.oodesigns.devicecomms.domain.communication.CommunicationClient;
import com.oodesigns.devicecomms.domain.communication.PointAddress;
import com.oodesigns.devicecomms.domain.communication.PointSnapshot;
import com.oodesigns.devicecomms.domain.device.Device;
import com.oodesigns.devicecomms.domain.device.DeviceCommand;
import com.oodesigns.devicecomms.domain.device.DeviceQuery;
import com.oodesigns.devicecomms.domain.device.DeviceState;
import com.oodesigns.devicecomms.domain.value.StartAddress;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.function.IntFunction;

public final class BlaubergMVHR implements Device {
    private record FailureReason(String value) {
        private FailureReason {
            if (value == null || value.isBlank()) {
                throw new IllegalArgumentException("failure reason must not be blank");
            }
        }
    }

    private static final CoilPoint CONTROLLER_POWER = new CoilPoint(new StartAddress(0));
    private static final CoilPoint UNIT_ON_OFF = new CoilPoint(new StartAddress(1));
    private static final CoilPoint WEEKLY_TIMER = new CoilPoint(new StartAddress(2));
    private static final HoldingRegisterPoint FAN_MODE = new HoldingRegisterPoint(new StartAddress(2));
    private static final HoldingRegisterPoint MANUAL_FAN_SPEED = new HoldingRegisterPoint(new StartAddress(17));
    private static final InputRegisterPoint SELECTED_TEMPERATURE = new InputRegisterPoint(new StartAddress(0));
    private static final InputRegisterPoint SUPPLY_AIR_TEMPERATURE = new InputRegisterPoint(new StartAddress(2));
    private static final InputRegisterPoint OUTDOOR_TEMPERATURE = new InputRegisterPoint(new StartAddress(5));
    private static final InputRegisterPoint INTERNAL_HUMIDITY = new InputRegisterPoint(new StartAddress(10));
    private static final InputRegisterPoint INTERNAL_CO2 = new InputRegisterPoint(new StartAddress(12));
    private static final InputRegisterPoint INTERNAL_PM25 = new InputRegisterPoint(new StartAddress(14));
    private static final InputRegisterPoint INTERNAL_VOC = new InputRegisterPoint(new StartAddress(16));
    private static final InputRegisterPoint SUPPLY_AIRFLOW = new InputRegisterPoint(new StartAddress(19));
    private static final InputRegisterPoint EXTRACT_AIRFLOW = new InputRegisterPoint(new StartAddress(20));
    private static final InputRegisterPoint SUPPLY_PRESSURE = new InputRegisterPoint(new StartAddress(21));
    private static final InputRegisterPoint EXTRACT_PRESSURE = new InputRegisterPoint(new StartAddress(22));
    private static final InputRegisterPoint SUPPLY_FAN_RPM = new InputRegisterPoint(new StartAddress(23));
    private static final InputRegisterPoint EXTRACT_FAN_RPM = new InputRegisterPoint(new StartAddress(24));
    private static final InputRegisterPoint FILTER_CONDITION = new InputRegisterPoint(new StartAddress(31));
    private static final InputRegisterPoint WEEKLY_SPEED = new InputRegisterPoint(new StartAddress(32));
    private static final InputRegisterPoint WEEKLY_TEMPERATURE = new InputRegisterPoint(new StartAddress(33));
    private static final InputRegisterPoint SUPPLY_AIR_TARGET = new InputRegisterPoint(new StartAddress(48));
    private static final List<PointAddress<?>> SNAPSHOT_POINTS = List.of(
            CONTROLLER_POWER, UNIT_ON_OFF, WEEKLY_TIMER, FAN_MODE,
            SELECTED_TEMPERATURE, SUPPLY_AIR_TEMPERATURE, OUTDOOR_TEMPERATURE,
            INTERNAL_HUMIDITY, INTERNAL_CO2, INTERNAL_PM25, INTERNAL_VOC,
            SUPPLY_AIRFLOW, EXTRACT_AIRFLOW, SUPPLY_PRESSURE, EXTRACT_PRESSURE,
            SUPPLY_FAN_RPM, EXTRACT_FAN_RPM, FILTER_CONDITION, WEEKLY_SPEED,
            WEEKLY_TEMPERATURE, SUPPLY_AIR_TARGET);

    private final CommunicationClient client;

    public BlaubergMVHR(final CommunicationClient client) {
        this.client = Objects.requireNonNull(client, "client");
    }

    @Override
    public CompletableFuture<Response<Void>> open() {
        try {
            return fromStage(client.connect(), new FailureReason("connection could not be opened"));
        } catch (final RuntimeException exception) {
            return completedFailure(detail(exception, new FailureReason("connection could not be opened")));
        }
    }

    @Override
    public CompletableFuture<Response<DeviceState>> read(final DeviceQuery query) {
        if (query == null) {
            return completedFailure(new FailureReason("MVHR query is required"));
        }
        if (query instanceof MvhrQuery mvhrQuery) {
            return read(mvhrQuery).thenApply(result -> result.map(state -> (DeviceState) state));
        }
        return completedFailure(new FailureReason("MVHR query is not supported"));
    }

    public CompletableFuture<Response<MvhrSnapshot>> read(final MvhrQuery query) {
        if (!(query instanceof ReadSnapshot)) {
            return completedFailure(new FailureReason("MVHR query is not supported"));
        }
        try {
            return fromStage(client.read(SNAPSHOT_POINTS), new FailureReason("MVHR snapshot read failed"))
                    .thenApply(result -> result.flatMap(BlaubergMVHR::decodeSnapshot));
        } catch (final RuntimeException exception) {
            return completedFailure(detail(exception, new FailureReason("MVHR snapshot read failed")));
        }
    }

    @Override
    public CompletableFuture<Response<Void>> execute(final DeviceCommand command) {
        if (command == null) {
            return completedFailure(new FailureReason("MVHR command is required"));
        }
        if (command instanceof SetControllerPower setControllerPower) {
            return write(CONTROLLER_POWER, setControllerPower.enabled());
        }
        if (command instanceof SetUnitOnOff setUnitOnOff) {
            return write(UNIT_ON_OFF, setUnitOnOff.enabled());
        }
        if (command instanceof SetFanMode setFanMode) {
            return write(FAN_MODE, setFanMode.mode().registerValue());
        }
        if (command instanceof SetManualFanSpeed setManualFanSpeed) {
            return write(FAN_MODE, 255).thenCompose(modeResult -> modeResult.fold(
                    ignored -> write(MANUAL_FAN_SPEED, setManualFanSpeed.speed().value()),
                    failure -> CompletableFuture.completedFuture(Response.failure(failure.details()))));
        }
        if (command instanceof SetSensorEnabled setSensorEnabled) {
            return write(setSensorEnabled.sensor().controlPoint(), setSensorEnabled.enabled());
        }
        if (command instanceof SetPresetAirflow setPresetAirflow) {
            final PresetAirflowSettings settings = setPresetAirflow.settings();
            return write(settings.preset().supplyFlowPoint(), settings.supply().value())
                    .thenCompose(supplyResult -> supplyResult.fold(
                            ignored -> write(settings.preset().extractFlowPoint(), settings.extract().value()),
                            failure -> CompletableFuture.completedFuture(Response.failure(failure.details()))));
        }
        if (command instanceof UpdateWeeklyTemperature updateWeeklyTemperature) {
            return updateWeeklyTemperature(updateWeeklyTemperature);
        }
        return completedFailure(new FailureReason("MVHR command is not supported"));
    }

    @Override
    public CompletableFuture<Response<Void>> close() {
        try {
            return fromStage(client.disconnect(), new FailureReason("connection could not be closed"));
        } catch (final RuntimeException exception) {
            return completedFailure(detail(exception, new FailureReason("connection could not be closed")));
        }
    }

    private <T> CompletableFuture<Response<Void>> write(final PointAddress<T> point, final T value) {
        try {
            return fromStage(client.write(point, value), new FailureReason("Modbus write failed"));
        } catch (final RuntimeException exception) {
            return completedFailure(detail(exception, new FailureReason("Modbus write failed")));
        }
    }

    private CompletableFuture<Response<Void>> updateWeeklyTemperature(
            final UpdateWeeklyTemperature command) {
        final HoldingRegisterPoint schedulePoint = command.slot().speedTemperatureRegister();
        try {
            return fromStage(client.read(List.of(schedulePoint)), new FailureReason("weekly schedule read failed"))
                    .thenCompose(readResult -> readResult.fold(
                            points -> updateTemperatureWord(points, schedulePoint, command.temperature()),
                            failure -> completedFailure(new FailureReason(failure.details()))));
        } catch (final RuntimeException exception) {
            return completedFailure(detail(exception, new FailureReason("weekly schedule read failed")));
        }
    }

    private CompletableFuture<Response<Void>> updateTemperatureWord(
            final PointSnapshot points,
            final HoldingRegisterPoint schedulePoint,
            final WeeklyTemperatureSetpointCelsius temperature) {
        try {
            return points.valueOf(schedulePoint).fold(
                    existingWord -> {
                        final int presetSpeed = existingWord >>> 8;
                        if (presetSpeed > 5) {
                            return completedFailure(new FailureReason("weekly schedule contains an invalid preset speed"));
                        }
                        final int updatedWord = (existingWord & 0xFF00) | (int) temperature.value();
                        return write(schedulePoint, updatedWord);
                    },
                        failure -> completedFailure(new FailureReason(failure.details())));
        } catch (final RuntimeException exception) {
                    return completedFailure(detail(exception, new FailureReason("weekly schedule word is invalid")));
        }
    }

    private static Response<MvhrSnapshot> decodeSnapshot(final PointSnapshot points) {
        if (points == null) {
            return Response.failure("MVHR snapshot response is missing");
        }
        try {
            return decodeSnapshotValues(points);
        } catch (final RuntimeException exception) {
                return Response.failure(detail(exception,
                    new FailureReason("MVHR snapshot contains an invalid value")).value());
        }
    }

    private static Response<MvhrSnapshot> decodeSnapshotValues(final PointSnapshot points) {
        final Response<Boolean> controllerPower = points.valueOf(CONTROLLER_POWER);
        final Response<Boolean> unitOn = points.valueOf(UNIT_ON_OFF);
        final Response<Boolean> weeklyTimer = points.valueOf(WEEKLY_TIMER);
        final Response<S21RegisterValue> fanMode = wireValue(points.valueOf(FAN_MODE));
        final Response<S21RegisterValue> selectedTemperature = wireValue(points.valueOf(SELECTED_TEMPERATURE));
        final Response<S21RegisterValue> supplyAirTemperature = wireValue(points.valueOf(SUPPLY_AIR_TEMPERATURE));
        final Response<S21RegisterValue> outdoorTemperature = wireValue(points.valueOf(OUTDOOR_TEMPERATURE));
        final Response<S21RegisterValue> internalHumidity = wireValue(points.valueOf(INTERNAL_HUMIDITY));
        final Response<S21RegisterValue> internalCo2 = wireValue(points.valueOf(INTERNAL_CO2));
        final Response<S21RegisterValue> internalPm25 = wireValue(points.valueOf(INTERNAL_PM25));
        final Response<S21RegisterValue> internalVoc = wireValue(points.valueOf(INTERNAL_VOC));
        final Response<S21RegisterValue> supplyAirflow = wireValue(points.valueOf(SUPPLY_AIRFLOW));
        final Response<S21RegisterValue> extractAirflow = wireValue(points.valueOf(EXTRACT_AIRFLOW));
        final Response<S21RegisterValue> supplyPressure = wireValue(points.valueOf(SUPPLY_PRESSURE));
        final Response<S21RegisterValue> extractPressure = wireValue(points.valueOf(EXTRACT_PRESSURE));
        final Response<S21RegisterValue> supplyRpm = wireValue(points.valueOf(SUPPLY_FAN_RPM));
        final Response<S21RegisterValue> extractRpm = wireValue(points.valueOf(EXTRACT_FAN_RPM));
        final Response<S21RegisterValue> filterCondition = wireValue(points.valueOf(FILTER_CONDITION));
        final Response<S21RegisterValue> weeklySpeed = wireValue(points.valueOf(WEEKLY_SPEED));
        final Response<S21RegisterValue> weeklyTemperature = wireValue(points.valueOf(WEEKLY_TEMPERATURE));
        final Response<S21RegisterValue> supplyAirTarget = wireValue(points.valueOf(SUPPLY_AIR_TARGET));
        final Response<?>[] values = {controllerPower, unitOn, weeklyTimer, fanMode,
            selectedTemperature, supplyAirTemperature, outdoorTemperature,
            internalHumidity, internalCo2, internalPm25, internalVoc,
            supplyAirflow, extractAirflow, supplyPressure, extractPressure,
            supplyRpm, extractRpm, filterCondition, weeklySpeed, weeklyTemperature, supplyAirTarget};
        for (final Response<?> value : values) {
            if (value.status() != Response.Status.OK) {
                return Response.failure(value.details());
            }
        }
        try {
            final Response<FanMode> decodedFanMode = decodeFanMode(fanMode.value());
            if (decodedFanMode.status() != Response.Status.OK) {
                return Response.failure(decodedFanMode.details());
            }
            final Response<ScheduledFanSpeed> decodedWeeklySpeed = decodeScheduledSpeed(weeklySpeed.value());
            if (decodedWeeklySpeed.status() != Response.Status.OK) {
                return Response.failure(decodedWeeklySpeed.details());
            }
            final Response<ActiveWeeklyTemperature> decodedTemperature = decodeWeeklyTemperature(
                    weeklyTemperature.value());
            if (decodedTemperature.status() != Response.Status.OK) {
                return Response.failure(decodedTemperature.details());
            }
                final Response<Optional<MvhrTemperatureCelsius>> decodedSelectedTemperature =
                    decodeTemperature(selectedTemperature.value());
                final Response<Optional<MvhrTemperatureCelsius>> decodedSupplyTemperature =
                    decodeTemperature(supplyAirTemperature.value());
                final Response<Optional<MvhrTemperatureCelsius>> decodedOutdoorTemperature =
                    decodeTemperature(outdoorTemperature.value());
                final Response<Optional<RelativeHumidityPercent>> decodedHumidity =
                    decodeOptionalReading(internalHumidity.value(), RelativeHumidityPercent::new);
                final Response<Optional<CarbonDioxidePpm>> decodedCo2 =
                    decodeOptionalReading(internalCo2.value(), CarbonDioxidePpm::new);
                final Response<Optional<Pm25Concentration>> decodedPm25 =
                    decodeOptionalReading(internalPm25.value(), Pm25Concentration::new);
                final Response<Optional<VocPercent>> decodedVoc =
                    decodeOptionalReading(internalVoc.value(), VocPercent::new);
                final Response<Optional<SupplyAirTargetCelsius>> decodedSupplyTarget =
                    decodeSupplyAirTarget(supplyAirTarget.value());
                final Response<?>[] decodedValues = {decodedSelectedTemperature, decodedSupplyTemperature,
                    decodedOutdoorTemperature, decodedHumidity, decodedCo2, decodedPm25, decodedVoc,
                    decodedSupplyTarget};
                for (final Response<?> value : decodedValues) {
                if (value.status() != Response.Status.OK) {
                    return Response.failure(value.details());
                }
                }
            return Response.success(new MvhrSnapshot(
                    new OperationSettings(controllerPower.value(), unitOn.value(), weeklyTimer.value()),
                    new FanSettings(decodedFanMode.value(), decodedWeeklySpeed.value(),
                        new FanRpm(supplyRpm.value().value()), new FanRpm(extractRpm.value().value()),
                        new AirflowRate(supplyAirflow.value().value()), new AirflowRate(extractAirflow.value().value()),
                        new DuctPressurePascal(supplyPressure.value().value()),
                        new DuctPressurePascal(extractPressure.value().value())),
                    new MaintenanceSettings(new FilterCondition(filterCondition.value().value())),
                    new ClimateSettings(decodedSelectedTemperature.value(), decodedSupplyTemperature.value(),
                        decodedOutdoorTemperature.value(), decodedSupplyTarget.value(),
                        decodedTemperature.value()),
                    new AirQualitySettings(decodedHumidity.value(), decodedCo2.value(), decodedPm25.value(),
                        decodedVoc.value())));
        } catch (final IllegalArgumentException exception) {
                return Response.failure(detail(exception,
                    new FailureReason("MVHR snapshot contains an invalid value")).value());
        }
    }

    private static Response<S21RegisterValue> wireValue(final Response<Integer> rawValue) {
        if (rawValue.status() != Response.Status.OK) {
            return Response.failure(rawValue.details());
        }
        try {
            return Response.success(new S21RegisterValue(rawValue.value()));
        } catch (final RuntimeException exception) {
            return Response.failure(detail(exception, new FailureReason("S21 register value is invalid")).value());
        }
    }

    private static Response<FanMode> decodeFanMode(final S21RegisterValue registerValue) {
        return switch (registerValue.value()) {
            case 1 -> Response.success(FanMode.SPEED_1);
            case 2 -> Response.success(FanMode.SPEED_2);
            case 3 -> Response.success(FanMode.SPEED_3);
            case 4 -> Response.success(FanMode.SPEED_4);
            case 5 -> Response.success(FanMode.SPEED_5);
            case 255 -> Response.success(FanMode.MANUAL);
            default -> Response.failure("unsupported S21 fan mode: " + registerValue.value());
        };
    }

    private static Response<ScheduledFanSpeed> decodeScheduledSpeed(final S21RegisterValue registerValue) {
        return switch (registerValue.value()) {
            case 0 -> Response.success(ScheduledFanSpeed.STANDBY);
            case 1 -> Response.success(ScheduledFanSpeed.SPEED_1);
            case 2 -> Response.success(ScheduledFanSpeed.SPEED_2);
            case 3 -> Response.success(ScheduledFanSpeed.SPEED_3);
            case 4 -> Response.success(ScheduledFanSpeed.SPEED_4);
            case 5 -> Response.success(ScheduledFanSpeed.SPEED_5);
            default -> Response.failure("unsupported S21 weekly speed: " + registerValue.value());
        };
    }

    private static Response<ActiveWeeklyTemperature> decodeWeeklyTemperature(final S21RegisterValue registerValue) {
        final int value = registerValue.value();
        if (value == 0) {
            return Response.success(ActiveWeeklyTemperature.ventilationOnly());
        }
        try {
            return Response.success(ActiveWeeklyTemperature.setpoint(
                    new WeeklyTemperatureSetpointCelsius(value)));
        } catch (final IllegalArgumentException exception) {
            return Response.failure(detail(exception, new FailureReason("invalid S21 weekly temperature")).value());
        }
    }

    private static Response<Optional<MvhrTemperatureCelsius>> decodeTemperature(
            final S21RegisterValue registerValue) {
        final int signedValue = registerValue.value() >= 32768
                ? registerValue.value() - 65536 : registerValue.value();
        if (signedValue == Short.MIN_VALUE) {
            return Response.success(Optional.empty());
        }
        if (signedValue == Short.MAX_VALUE) {
            return Response.failure("temperature sensor reports a short circuit");
        }
        try {
            return Response.success(Optional.of(new MvhrTemperatureCelsius(signedValue / 10.0)));
        } catch (final IllegalArgumentException exception) {
            return Response.failure(detail(exception, new FailureReason("temperature sensor value is invalid")).value());
        }
    }

    private static <T> Response<Optional<T>> decodeOptionalReading(
            final S21RegisterValue registerValue, final IntFunction<T> constructor) {
        if (registerValue.value() == 0) {
            return Response.success(Optional.empty());
        }
        try {
            return Response.success(Optional.of(constructor.apply(registerValue.value())));
        } catch (final IllegalArgumentException exception) {
            return Response.failure(detail(exception, new FailureReason("S21 sensor reading is invalid")).value());
        }
    }

    private static Response<Optional<SupplyAirTargetCelsius>> decodeSupplyAirTarget(
            final S21RegisterValue registerValue) {
        try {
            return Response.success(Optional.of(new SupplyAirTargetCelsius(registerValue.value() / 10.0)));
        } catch (final IllegalArgumentException exception) {
            return Response.failure(detail(exception, new FailureReason("supply air target is invalid")).value());
        }
    }

    private static <T> CompletableFuture<Response<T>> fromStage(
            final CompletionStage<Response<T>> stage, final FailureReason fallback) {
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
                return Response.<T>failure(detail(cause, fallback).value());
            }
            return result == null ? Response.<T>failure(fallback.value()) : result;
        }).toCompletableFuture();
    }

    private static <T> CompletableFuture<Response<T>> completedFailure(final FailureReason reason) {
        return CompletableFuture.completedFuture(Response.failure(reason.value()));
    }

    private static FailureReason detail(final Throwable exception, final FailureReason fallback) {
        final String message = exception.getMessage();
        return message == null || message.isBlank() ? fallback : new FailureReason(message);
    }
}