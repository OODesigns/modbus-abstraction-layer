package com.oodesigns.devicecomms.device.waveshare.analogoutput.configuration;

import com.oodesigns.devicecomms.device.waveshare.analogoutput.AnalogOutput8CH;
import com.oodesigns.devicecomms.device.waveshare.analogoutput.profile.WaveshareAnalogOutputProfile;
import com.oodesigns.devicecomms.domain.Response;
import com.oodesigns.devicecomms.domain.device.ConfigLoader;
import com.oodesigns.devicecomms.domain.device.Dependencies;
import com.oodesigns.devicecomms.domain.device.Device;
import com.oodesigns.devicecomms.domain.device.DevicePlugin;
import com.oodesigns.devicecomms.domain.value.DependencyKey;
import com.oodesigns.devicecomms.domain.value.DeviceType;
import java.util.Set;

public final class WaveshareAnalogOutput8CHPlugin implements DevicePlugin {
    private static final DeviceType DEVICE_TYPE = new DeviceType("waveshare-modbus-rtu-analog-output-8ch-b");

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
            return Response.failure("Waveshare analog-output configuration and dependencies are required");
        }
        final WaveshareAnalogOutputProfile profile;
        try {
            profile = new WaveshareAnalogOutputProfile(config);
        } catch (final RuntimeException exception) {
            final String message = exception.getMessage();
            return Response.failure(message == null || message.isBlank()
                    ? "Waveshare analog-output profile is invalid" : message);
        }
        return dependencies.communicationRegistry().get(profile.transport(), profile.endpoint())
                .map(AnalogOutput8CH::new);
    }
}