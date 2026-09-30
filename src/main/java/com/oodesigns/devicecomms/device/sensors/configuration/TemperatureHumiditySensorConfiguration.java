package com.oodesigns.devicecomms.device.sensors.configuration;

import com.oodesigns.devicecomms.device.waveshare.analoginput.port.AnalogInputReader;
import com.oodesigns.devicecomms.device.sensors.profile.TemperatureHumiditySensorProfile;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class TemperatureHumiditySensorConfiguration {
    @Bean
    public TemperatureHumiditySensors temperatureHumiditySensors(final AnalogInputReader inputReader,
                                                                   final List<TemperatureHumiditySensorProfile> profiles) {
        return new TemperatureHumiditySensors(inputReader, profiles);
    }
}