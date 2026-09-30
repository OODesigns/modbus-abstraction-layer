package com.oodesigns.devicecomms.device.sensors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.oodesigns.devicecomms.device.sensors.configuration.SensorComponentsConfiguration;
import com.oodesigns.devicecomms.device.sensors.configuration.TemperatureHumiditySensorConfiguration;
import com.oodesigns.devicecomms.device.sensors.configuration.TemperatureHumiditySensors;
import com.oodesigns.devicecomms.device.sensors.profile.CarbonDioxideRange;
import com.oodesigns.devicecomms.device.sensors.profile.ElectricalSignalMode;
import com.oodesigns.devicecomms.device.sensors.profile.ParticulateRange;
import com.oodesigns.devicecomms.device.sensors.profile.ParticulateSize;
import com.oodesigns.devicecomms.device.sensors.profile.RoomSensorProfile;
import com.oodesigns.devicecomms.device.sensors.profile.SensorChannelProfile;
import com.oodesigns.devicecomms.device.sensors.profile.SensorIdentity;
import com.oodesigns.devicecomms.device.sensors.profile.SensorLocation;
import com.oodesigns.devicecomms.device.sensors.profile.SensorMeasurementRange;
import com.oodesigns.devicecomms.device.sensors.profile.SensorName;
import com.oodesigns.devicecomms.device.sensors.profile.SensorSignalScale;
import com.oodesigns.devicecomms.device.sensors.profile.TemperatureHumiditySensorProfile;
import com.oodesigns.devicecomms.device.waveshare.analoginput.port.AnalogInputReader;
import com.oodesigns.devicecomms.device.waveshare.analoginput.reading.AnalogChannelReading;
import com.oodesigns.devicecomms.device.waveshare.analoginput.reading.AnalogInputSnapshot;
import com.oodesigns.devicecomms.device.waveshare.analoginput.value.AnalogChannelNumber;
import com.oodesigns.devicecomms.device.waveshare.analoginput.value.AnalogInputRangeMode;
import com.oodesigns.devicecomms.device.waveshare.analoginput.value.CurrentMicroamps;
import com.oodesigns.devicecomms.device.waveshare.analoginput.value.VoltageMillivolts;
import com.oodesigns.devicecomms.domain.Response;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

class SensorComponentsConfigurationTest {
    @Test
    void springInjectsSharedAnalogInputReaderIntoGenericSensorClasses() {
        final AnalogInputReader inputStream = () -> CompletableFuture.completedFuture(
                Response.success(new AnalogInputSnapshot(List.of(
                        voltageReading(1, 5000), voltageReading(2, 7500),
                        voltageReading(3, 5000), voltageReading(4, 5000),
                        voltageReading(5, 0), voltageReading(6, 0),
                        voltageReading(7, 0), voltageReading(8, 0)))));

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.registerBean(AnalogInputReader.class, () -> inputStream);
            context.registerBean(RoomSensorProfile.class, SensorComponentsConfigurationTest::voltageProfile);
            context.register(SensorComponentsConfiguration.class);
            context.refresh();

            final TemperatureSensor temperature = context.getBean(TemperatureSensor.class);
            final HumiditySensor humidity = context.getBean(HumiditySensor.class);
            final ParticulateMatterSensor particulate = context.getBean(ParticulateMatterSensor.class);
            final CarbonDioxideSensor carbonDioxide = context.getBean(CarbonDioxideSensor.class);

            assertSame(inputStream, temperature.inputReader());
            assertSame(inputStream, humidity.inputReader());
            assertSame(inputStream, particulate.inputReader());
            assertSame(inputStream, carbonDioxide.inputReader());
            assertEquals(25.0, temperature.read().join().value().value().value());
            assertEquals(75.0, humidity.read().join().value().value().value());
            assertEquals(250.0, particulate.read().join().value().value().value());
            assertEquals(1000, carbonDioxide.read().join().value().value().value());
        }
    }

    @Test
    void genericSensorsScaleCurrentSignalsWithoutVendorSpecificTypes() {
        final AnalogInputReader inputStream = () -> CompletableFuture.completedFuture(
                Response.success(new AnalogInputSnapshot(List.of(
                        currentReading(1, 12000), currentReading(2, 12000),
                        currentReading(3, 12000), currentReading(4, 12000),
                        currentReading(5, 0), currentReading(6, 0),
                        currentReading(7, 0), currentReading(8, 0)))));

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.registerBean(AnalogInputReader.class, () -> inputStream);
            context.registerBean(RoomSensorProfile.class, SensorComponentsConfigurationTest::currentProfile);
            context.register(SensorComponentsConfiguration.class);
            context.refresh();

            assertEquals(25.0, context.getBean(TemperatureSensor.class).read().join().value().value().value());
            assertEquals(50.0, context.getBean(HumiditySensor.class).read().join().value().value().value());
            assertEquals(250.0, context.getBean(ParticulateMatterSensor.class).read().join().value().value().value());
            assertEquals(1000, context.getBean(CarbonDioxideSensor.class).read().join().value().value().value());
        }
    }

    @Test
    void temperatureHumidityProfileWiresOnlyItsConfiguredChannels() {
        final AnalogInputReader inputReader = () -> CompletableFuture.completedFuture(
                Response.success(new AnalogInputSnapshot(List.of(
                        voltageReading(1, 5000), voltageReading(2, 7500),
                        voltageReading(3, 0), voltageReading(4, 0),
                        voltageReading(5, 0), voltageReading(6, 0),
                        voltageReading(7, 0), voltageReading(8, 0)))));

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.registerBean(AnalogInputReader.class, () -> inputReader);
            context.registerBean(TemperatureHumiditySensorProfile.class,
                    () -> profile("test-room", 1, 2, ElectricalSignalMode.VOLTAGE_0_TO_10));
            context.register(TemperatureHumiditySensorConfiguration.class);
            context.refresh();

            final TemperatureHumiditySensors sensors = context.getBean(TemperatureHumiditySensors.class);
            assertEquals(25.0, sensors.temperatures().get(identity("temperature", "test-room"))
                    .read().join().value().value().value());
            assertEquals(75.0, sensors.humidities().get(identity("humidity", "test-room"))
                    .read().join().value().value().value());
        }
    }

    @Test
    void temperatureHumidityProfileSupportsLoopPoweredCurrentOutputs() {
        final AnalogInputReader inputReader = () -> CompletableFuture.completedFuture(
                Response.success(new AnalogInputSnapshot(List.of(
                        currentReading(1, 12000), currentReading(2, 12000),
                        currentReading(3, 0), currentReading(4, 0),
                        currentReading(5, 0), currentReading(6, 0),
                        currentReading(7, 0), currentReading(8, 0)))));

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.registerBean(AnalogInputReader.class, () -> inputReader);
            context.registerBean(TemperatureHumiditySensorProfile.class,
                    () -> profile("test-room", 1, 2, ElectricalSignalMode.CURRENT_4_TO_20));
            context.register(TemperatureHumiditySensorConfiguration.class);
            context.refresh();

            final TemperatureHumiditySensors sensors = context.getBean(TemperatureHumiditySensors.class);
            assertEquals(25.0, sensors.temperatures().get(identity("temperature", "test-room"))
                    .read().join().value().value().value());
            assertEquals(50.0, sensors.humidities().get(identity("humidity", "test-room"))
                    .read().join().value().value().value());
        }
    }

    @Test
    void temperatureHumidityProfileRejectsReusingOneOutputChannel() {
        assertThrows(IllegalArgumentException.class, () -> new TemperatureHumiditySensorProfile(
                identity("temperature", "test-room"), new AnalogChannelNumber(1),
                identity("humidity", "test-room"), new AnalogChannelNumber(1),
                ElectricalSignalMode.VOLTAGE_0_TO_10));
    }

    @Test
    void springCreatesNamedHumidityInstancesAndReturnsIdentityWithEachReading() {
        final AnalogInputReader inputReader = () -> CompletableFuture.completedFuture(
                Response.success(new AnalogInputSnapshot(List.of(
                        voltageReading(1, 5000), voltageReading(2, 7500),
                        voltageReading(3, 2500), voltageReading(4, 2500),
                        voltageReading(5, 0), voltageReading(6, 0),
                        voltageReading(7, 0), voltageReading(8, 0)))));
        final SensorIdentity bathroomHumidity = identity("humidity", "bathroom");
        final SensorIdentity kitchenHumidity = identity("humidity", "kitchen");

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.registerBean(AnalogInputReader.class, () -> inputReader);
            context.registerBean("bathroomProfile", TemperatureHumiditySensorProfile.class,
                    () -> profile("bathroom", 1, 2, ElectricalSignalMode.VOLTAGE_0_TO_10));
            context.registerBean("kitchenProfile", TemperatureHumiditySensorProfile.class,
                    () -> profile("kitchen", 3, 4, ElectricalSignalMode.VOLTAGE_0_TO_10));
            context.register(TemperatureHumiditySensorConfiguration.class);
            context.refresh();

            final TemperatureHumiditySensors sensors = context.getBean(TemperatureHumiditySensors.class);
            final var bathroomReading = sensors.humidities().get(bathroomHumidity).read().join().value();
            final var kitchenReading = sensors.humidities().get(kitchenHumidity).read().join().value();

            assertEquals(bathroomHumidity, bathroomReading.identity());
            assertEquals(75.0, bathroomReading.value().value());
            assertEquals(kitchenHumidity, kitchenReading.identity());
            assertEquals(25.0, kitchenReading.value().value());
        }
    }

        @Test
        void temperatureHumiditySensorsRejectDuplicateIdentities() {
                final AnalogInputReader inputReader = () -> CompletableFuture.completedFuture(
                                Response.success(new AnalogInputSnapshot(List.of())));
                final List<TemperatureHumiditySensorProfile> profiles = List.of(
                                profile("bathroom", 1, 2, ElectricalSignalMode.VOLTAGE_0_TO_10),
                                profile("bathroom", 3, 4, ElectricalSignalMode.VOLTAGE_0_TO_10));

                assertThrows(IllegalArgumentException.class, () -> new TemperatureHumiditySensors(inputReader, profiles));
        }

    private static TemperatureHumiditySensorProfile profile(final String location,
                                                            final int temperatureChannel,
                                                            final int humidityChannel,
                                                            final ElectricalSignalMode mode) {
        return new TemperatureHumiditySensorProfile(
                identity("temperature", location), new AnalogChannelNumber(temperatureChannel),
                identity("humidity", location), new AnalogChannelNumber(humidityChannel),
                mode);
    }

    private static SensorIdentity identity(final String name, final String location) {
        return new SensorIdentity(new SensorName(name), new SensorLocation(location));
    }

    private static RoomSensorProfile voltageProfile() {
        return profile(ElectricalSignalMode.VOLTAGE_0_TO_10);
    }

    private static RoomSensorProfile currentProfile() {
        return profile(ElectricalSignalMode.CURRENT_4_TO_20);
    }

    private static RoomSensorProfile profile(final ElectricalSignalMode mode) {
        return new RoomSensorProfile(
                channelProfile("temperature", 1, mode, 0, 50),
                channelProfile("humidity", 2, mode, 0, 100),
                channelProfile("particulate", 3, mode, 0, 500),
                channelProfile("carbon-dioxide", 4, mode, 0, 2000),
                ParticulateRange.MICROGRAMS_0_TO_500,
                ParticulateSize.PM_2_5,
                CarbonDioxideRange.PPM_0_TO_2000);
    }

        private static SensorChannelProfile channelProfile(final String name,
                                                                                                           final int channel,
                                                       final ElectricalSignalMode mode,
                                                       final double physicalMinimum,
                                                       final double physicalMaximum) {
                return new SensorChannelProfile(identity(name, "test-room"),
                new AnalogChannelNumber(channel),
                new SensorSignalScale(mode,
                        new SensorMeasurementRange(physicalMinimum, physicalMaximum)));
    }

    private static AnalogChannelReading voltageReading(final int channel, final int value) {
        return new AnalogChannelReading(new AnalogChannelNumber(channel),
                AnalogInputRangeMode.VOLTAGE_ZERO_BASED, new VoltageMillivolts(value));
    }

    private static AnalogChannelReading currentReading(final int channel, final int value) {
        return new AnalogChannelReading(new AnalogChannelNumber(channel),
                AnalogInputRangeMode.CURRENT_OFFSET, new CurrentMicroamps(value));
    }
}