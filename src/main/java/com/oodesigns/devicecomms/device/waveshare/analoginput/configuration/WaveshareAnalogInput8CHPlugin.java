package com.oodesigns.devicecomms.device.waveshare.analoginput.configuration;

import com.oodesigns.devicecomms.device.waveshare.analoginput.AnalogInput8CH;
import com.oodesigns.devicecomms.device.waveshare.analoginput.profile.WaveshareAnalogInputProfile;
import com.oodesigns.devicecomms.domain.Response;
import com.oodesigns.devicecomms.domain.device.ConfigLoader;
import com.oodesigns.devicecomms.domain.device.Dependencies;
import com.oodesigns.devicecomms.domain.device.Device;
import com.oodesigns.devicecomms.domain.device.DevicePlugin;
import com.oodesigns.devicecomms.domain.value.DependencyKey;
import com.oodesigns.devicecomms.domain.value.DeviceType;
import java.util.Set;

public final class WaveshareAnalogInput8CHPlugin implements DevicePlugin {
    private static final DeviceType DEVICE_TYPE = new DeviceType("waveshare-modbus-rtu-analog-input-8ch");

    @Override
    public DeviceType deviceType() {
        return DEVICE_TYPE;
    }

    @Override
    public Set<DependencyKey> requiredDependencies() {
        return Set.of(Dependencies.COMMUNICATION_REGISTRY);
    }

    @Override
    public Response<Device> create(final ConfigLoader config, final Dependencies dependencies) {
        if (config == null || dependencies == null) {
            return Response.failure("Waveshare analog-input profile and dependencies are required");
        }
        final WaveshareAnalogInputProfile profile;
        try {
            profile = new WaveshareAnalogInputProfile(config);
        } catch (final RuntimeException exception) {
            final String message = exception.getMessage();
            return Response.failure(message == null || message.isBlank()
                    ? "Waveshare analog-input profile is invalid" : message);
        }
        return dependencies.communicationRegistry().get(profile.transport(), profile.endpoint())
                .map(client -> new AnalogInput8CH(client, profile.analogInputProfile()));
    }
}