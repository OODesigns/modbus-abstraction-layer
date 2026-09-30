package com.oodesigns.devicecomms.device.sensors.configuration;

import com.oodesigns.devicecomms.device.sensors.HumiditySensor;
import com.oodesigns.devicecomms.device.sensors.TemperatureSensor;
import com.oodesigns.devicecomms.device.sensors.profile.SensorIdentity;
import com.oodesigns.devicecomms.device.sensors.profile.TemperatureHumiditySensorProfile;
import com.oodesigns.devicecomms.device.waveshare.analoginput.value.AnalogChannelNumber;
import com.oodesigns.devicecomms.device.waveshare.analoginput.port.AnalogInputReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class TemperatureHumiditySensors {
    private final Map<SensorIdentity, TemperatureSensor> temperatures;
    private final Map<SensorIdentity, HumiditySensor> humidities;

    public TemperatureHumiditySensors(final AnalogInputReader inputReader,
                                      final List<TemperatureHumiditySensorProfile> profiles) {
        Objects.requireNonNull(inputReader, "inputReader");
        final List<TemperatureHumiditySensorProfile> configuredProfiles = List.copyOf(profiles);
        requireUniqueIdentities(configuredProfiles);
        requireUniqueChannels(configuredProfiles);
        temperatures = temperatureSensors(inputReader, configuredProfiles);
        humidities = humiditySensors(inputReader, configuredProfiles);
    }

    public Map<SensorIdentity, TemperatureSensor> temperatures() {
        return temperatures;
    }

    public Map<SensorIdentity, HumiditySensor> humidities() {
        return humidities;
    }

    private static Map<SensorIdentity, TemperatureSensor> temperatureSensors(
            final AnalogInputReader inputReader, final List<TemperatureHumiditySensorProfile> profiles) {
        final Map<SensorIdentity, TemperatureSensor> sensors = new HashMap<>();
        profiles.forEach(profile -> {
            final var channel = profile.temperatureChannelProfile();
            sensors.put(channel.identity(), new TemperatureSensor(inputReader, channel));
        });
        return Map.copyOf(sensors);
    }

    private static Map<SensorIdentity, HumiditySensor> humiditySensors(
            final AnalogInputReader inputReader, final List<TemperatureHumiditySensorProfile> profiles) {
        final Map<SensorIdentity, HumiditySensor> sensors = new HashMap<>();
        profiles.forEach(profile -> {
            final var channel = profile.humidityChannelProfile();
            sensors.put(channel.identity(), new HumiditySensor(inputReader, channel));
        });
        return Map.copyOf(sensors);
    }

    private static void requireUniqueIdentities(final List<TemperatureHumiditySensorProfile> profiles) {
        final List<SensorIdentity> identities = profiles.stream()
                .flatMap(profile -> List.of(profile.temperatureIdentity(), profile.humidityIdentity()).stream())
                .toList();
        if (identities.stream().distinct().count() != identities.size()) {
            throw new IllegalArgumentException("sensor identities must be unique");
        }
    }

    private static void requireUniqueChannels(final List<TemperatureHumiditySensorProfile> profiles) {
        final List<AnalogChannelNumber> channels = profiles.stream()
                .flatMap(profile -> List.of(profile.temperatureChannel(), profile.humidityChannel()).stream())
                .toList();
        if (channels.stream().distinct().count() != channels.size()) {
            throw new IllegalArgumentException("each sensor instance must use distinct analog channels");
        }
    }
}