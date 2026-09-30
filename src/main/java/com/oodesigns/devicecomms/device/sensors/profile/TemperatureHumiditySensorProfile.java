package com.oodesigns.devicecomms.device.sensors.profile;

import com.oodesigns.devicecomms.device.waveshare.analoginput.value.AnalogChannelNumber;
import java.util.Objects;

public record TemperatureHumiditySensorProfile(SensorIdentity temperatureIdentity,
                                                AnalogChannelNumber temperatureChannel,
                                                SensorIdentity humidityIdentity,
                                                AnalogChannelNumber humidityChannel,
                                                ElectricalSignalMode electricalSignalMode) {
    public TemperatureHumiditySensorProfile {
        Objects.requireNonNull(temperatureIdentity, "temperatureIdentity");
        Objects.requireNonNull(temperatureChannel, "temperatureChannel");
        Objects.requireNonNull(humidityIdentity, "humidityIdentity");
        Objects.requireNonNull(humidityChannel, "humidityChannel");
        Objects.requireNonNull(electricalSignalMode, "electricalSignalMode");
        if (temperatureIdentity.equals(humidityIdentity)) {
            throw new IllegalArgumentException("temperature and humidity sensors must have distinct identities");
        }
        if (temperatureChannel.equals(humidityChannel)) {
            throw new IllegalArgumentException("temperature and humidity outputs must use distinct channels");
        }
    }

    public SensorChannelProfile temperatureChannelProfile() {
        return new SensorChannelProfile(temperatureIdentity, temperatureChannel, new SensorSignalScale(
                electricalSignalMode, new SensorMeasurementRange(0, 50)));
    }

    public SensorChannelProfile humidityChannelProfile() {
        return new SensorChannelProfile(humidityIdentity, humidityChannel, new SensorSignalScale(
                electricalSignalMode, new SensorMeasurementRange(0, 100)));
    }
}