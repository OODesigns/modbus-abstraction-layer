# Analog Room Sensor Components

The generic sensor components live in `com.oodesigns.devicecomms.device.sensors`:

- `TemperatureSensor`
- `HumiditySensor`
- `ParticulateMatterSensor`
- `CarbonDioxideSensor`

Each sensor receives the shared `AnalogInputReader` port through its
constructor. Sensor classes do not depend on a manufacturer name, Modbus point,
or device-specific register. Reading value objects live in
`com.oodesigns.devicecomms.device.sensors.reading` (including channel reading
support), channel and signal profiles
in `com.oodesigns.devicecomms.device.sensors.profile`, and Spring wiring in
`com.oodesigns.devicecomms.device.sensors.configuration`. The application
provides an `AnalogInputReader` bean and a `RoomSensorProfile` bean to the
Spring context. Each channel profile carries a `SensorIdentity`; successful
reads return `SensorReading<T>` with that identity and its typed value.

## Wiring with Spring

The Waveshare board is configured as a separate component and injected through
its interface. The communication client shown below is expected to be created
from the application's Modbus RTU registry/profile; no sensor bean creates a
serial connection itself.

```java
@Configuration(proxyBeanMethods = false)
@Import(SensorComponentsConfiguration.class)
class RoomSensorsConfiguration {
    @Bean
    AnalogInputProfile analogInputProfile() {
        return new AnalogInputProfile(ModuleRevision.B);
    }

    @Bean
    AnalogInputReader analogInputReader(CommunicationClient client, AnalogInputProfile inputProfile) {
        return new AnalogInput8CH(client, inputProfile);
    }

    @Bean
    RoomSensorProfile roomSensorProfile() {
        return new RoomSensorProfile(
            new SensorChannelProfile(new SensorIdentity(new SensorName("temperature"),
                new SensorLocation("living-room")), new AnalogChannelNumber(1),
                new SensorSignalScale(ElectricalSignalMode.VOLTAGE_0_TO_10,
                    new SensorMeasurementRange(0, 50))),
            new SensorChannelProfile(new SensorIdentity(new SensorName("humidity"),
                new SensorLocation("living-room")), new AnalogChannelNumber(2),
                new SensorSignalScale(ElectricalSignalMode.VOLTAGE_0_TO_10,
                    new SensorMeasurementRange(0, 100))),
            new SensorChannelProfile(new SensorIdentity(new SensorName("particulate"),
                new SensorLocation("living-room")), new AnalogChannelNumber(3),
                new SensorSignalScale(ElectricalSignalMode.VOLTAGE_0_TO_10,
                    new SensorMeasurementRange(0, 500))),
            new SensorChannelProfile(new SensorIdentity(new SensorName("carbon-dioxide"),
                new SensorLocation("living-room")), new AnalogChannelNumber(4),
                new SensorSignalScale(ElectricalSignalMode.VOLTAGE_0_TO_10,
                    new SensorMeasurementRange(0, 2000))),
            ParticulateRange.MICROGRAMS_0_TO_500,
            ParticulateSize.PM_2_5,
            CarbonDioxideRange.PPM_0_TO_2000);
    }
}
```

The channel numbers `1`-`4` above are illustrative wiring assignments only.
Change them to match the actual terminals. The shared `CommunicationClient`
bean must be created from the RTU communication registry/profile by the parent
application; the reader is then injected into every sensor bean.

Once Spring starts the context, a parent/composite device can inject one or
more sensor components and use their typed reads:

```java
final class AirQualityDevice {
    private final TemperatureSensor temperature;
    private final HumiditySensor humidity;
    private final ParticulateMatterSensor particulate;
    private final CarbonDioxideSensor carbonDioxide;

    AirQualityDevice(TemperatureSensor temperature,
                     HumiditySensor humidity,
                     ParticulateMatterSensor particulate,
                     CarbonDioxideSensor carbonDioxide) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.particulate = particulate;
        this.carbonDioxide = carbonDioxide;
    }
}
```

## Configuring Analog Room Sensors

For a room sensor that provides temperature, relative humidity, particulate
matter, and CO2 as separate analog outputs, provide each installed channel in
`RoomSensorProfile`. Channel assignments and output ranges depend on the
specific device and wiring, so keep them in the profile rather than in the
generic sensor classes.

Use a matching `ElectricalSignalMode` for each channel scale and set measurement
ranges from the connected sensor's documentation. For voltage, configure the
Waveshare channel for its supported voltage range. For current, configure it
for the corresponding current range. Some sensors share one output mode across
all outputs; others may allow per-channel selection.

The generic `SensorSignalScale` assumes a nominal linear output and currently
does not compensate for adjusted offsets. Account for calibration offsets
during commissioning before relying on absolute measurements. Published
accuracy is a property of the physical sensor; these reading types do not
currently attach uncertainty metadata to individual readings.

## Temperature and Humidity Outputs

`TemperatureHumiditySensorConfiguration` collects every
`TemperatureHumiditySensorProfile` bean and exposes maps of sensors keyed by
`SensorIdentity`. That supports multiple rooms on one shared analog reader:

```java
@Bean
TemperatureHumiditySensorProfile bathroomSensorsProfile() {
    return new TemperatureHumiditySensorProfile(
        new SensorIdentity(new SensorName("temperature"), new SensorLocation("bathroom")),
        new AnalogChannelNumber(1),
        new SensorIdentity(new SensorName("humidity"), new SensorLocation("bathroom")),
        new AnalogChannelNumber(2),
        ElectricalSignalMode.VOLTAGE_0_TO_10);
}

@Bean
TemperatureHumiditySensorProfile kitchenSensorsProfile() {
    return new TemperatureHumiditySensorProfile(
        new SensorIdentity(new SensorName("temperature"), new SensorLocation("kitchen")),
        new AnalogChannelNumber(3),
        new SensorIdentity(new SensorName("humidity"), new SensorLocation("kitchen")),
        new AnalogChannelNumber(4),
        ElectricalSignalMode.VOLTAGE_0_TO_10);
}

```

Use actual channel wiring and select voltage or current mode to match the
connected device. Access a specific instance through its identity, for example
`sensors.humidities().get(new SensorIdentity(new SensorName("humidity"),
new SensorLocation("bathroom")))`. The returned
`SensorReading<RoomHumidityPercent>` retains that identity alongside the
measurement. Identity names and locations must be unique, and channels cannot
be assigned to multiple configured sensor instances. An additional resistive
temperature output requires a resistance-measurement input and its own profile.
