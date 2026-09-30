package com.oodesigns.devicecomms.testkit;

import com.oodesigns.devicecomms.domain.Response;
import com.oodesigns.devicecomms.domain.device.ConfigLoader;
import com.oodesigns.devicecomms.domain.device.Dependencies;
import com.oodesigns.devicecomms.domain.device.Device;
import com.oodesigns.devicecomms.domain.device.DevicePlugin;
import com.oodesigns.devicecomms.domain.value.DependencyKey;
import com.oodesigns.devicecomms.domain.value.DeviceType;
import java.util.Set;

public final class TestDevicePlugin implements DevicePlugin {
    public static final DeviceType TYPE = new DeviceType("test-device");

    @Override
    public DeviceType deviceType() {
        return TYPE;
    }

    @Override
    public Set<DependencyKey> requiredDependencies() {
        return Set.of();
    }

    @Override
    public Response<Device> create(final ConfigLoader config, final Dependencies dependencies) {
        return Response.success(new TestDevice(config));
    }
}