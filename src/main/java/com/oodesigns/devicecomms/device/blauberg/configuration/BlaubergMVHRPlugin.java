package com.oodesigns.devicecomms.device.blauberg.configuration;

import com.oodesigns.devicecomms.device.blauberg.BlaubergMVHR;
import com.oodesigns.devicecomms.device.blauberg.profile.BlaubergConnectionProfile;
import com.oodesigns.devicecomms.domain.Response;
import com.oodesigns.devicecomms.domain.device.ConfigLoader;
import com.oodesigns.devicecomms.domain.device.Dependencies;
import com.oodesigns.devicecomms.domain.device.Device;
import com.oodesigns.devicecomms.domain.device.DevicePlugin;
import com.oodesigns.devicecomms.domain.value.DependencyKey;
import com.oodesigns.devicecomms.domain.value.DeviceType;
import java.util.Set;

public final class BlaubergMVHRPlugin implements DevicePlugin {
    private static final DeviceType DEVICE_TYPE = new DeviceType("blauberg-mvhr");
    private static final DependencyKey COMMUNICATION_REGISTRY = Dependencies.COMMUNICATION_REGISTRY;
    @Override
    public DeviceType deviceType() {
        return DEVICE_TYPE;
    }

    @Override
    public Set<DependencyKey> requiredDependencies() {
        return Set.of(COMMUNICATION_REGISTRY);
    }

    @Override
    public Response<Device> create(final ConfigLoader config, final Dependencies dependencies) {
        if (config == null || dependencies == null) {
            return Response.failure("Blauberg MVHR configuration and dependencies are required");
        }
        final BlaubergConnectionProfile profile;
        try {
            profile = new BlaubergConnectionProfile(config);
        } catch (final RuntimeException exception) {
            return Response.failure(detail(exception));
        }
        return dependencies.communicationRegistry().get(profile.transport(), profile.endpoint())
                .map(BlaubergMVHR::new);
    }

    private static String detail(final RuntimeException exception) {
        final String message = exception.getMessage();
        return message == null || message.isBlank() ? "Blauberg MVHR profile is invalid" : message;
    }

}