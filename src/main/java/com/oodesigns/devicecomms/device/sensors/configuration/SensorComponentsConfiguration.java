package com.oodesigns.devicecomms.device.sensors.configuration;

import com.oodesigns.devicecomms.device.sensors.CarbonDioxideSensor;
import com.oodesigns.devicecomms.device.sensors.HumiditySensor;
import com.oodesigns.devicecomms.device.sensors.ParticulateMatterSensor;
import com.oodesigns.devicecomms.device.sensors.TemperatureSensor;
import com.oodesigns.devicecomms.device.waveshare.analoginput.port.AnalogInputReader;
import com.oodesigns.devicecomms.device.sensors.profile.RoomSensorProfile;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class SensorComponentsConfiguration {
    @Bean
    public TemperatureSensor temperatureSensor(final AnalogInputReader inputReader,
                                               final RoomSensorProfile profile) {
        return new TemperatureSensor(inputReader, profile.temperature());
    }

    @Bean
    public HumiditySensor humiditySensor(final AnalogInputReader inputReader,
                                         final RoomSensorProfile profile) {
        return new HumiditySensor(inputReader, profile.humidity());
    }

    @Bean
    public ParticulateMatterSensor particulateMatterSensor(final AnalogInputReader inputReader,
                                                           final RoomSensorProfile profile) {
        return new ParticulateMatterSensor(inputReader, profile.particulate(),
                profile.particulateRange(), profile.particulateSize());
    }

    @Bean
    public CarbonDioxideSensor carbonDioxideSensor(final AnalogInputReader inputReader,
                                                   final RoomSensorProfile profile) {
        return new CarbonDioxideSensor(inputReader, profile.carbonDioxide(), profile.carbonDioxideRange());
    }
}