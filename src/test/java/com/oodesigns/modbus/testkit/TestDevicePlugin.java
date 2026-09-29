package com.oodesigns.modbus.testkit;

import com.oodesigns.modbus.domain.Response;
import com.oodesigns.modbus.domain.device.ConfigLoader;
import com.oodesigns.modbus.domain.device.Dependencies;
import com.oodesigns.modbus.domain.device.Device;
import com.oodesigns.modbus.domain.device.DevicePlugin;
import com.oodesigns.modbus.domain.value.DependencyKey;
import com.oodesigns.modbus.domain.value.DeviceType;
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