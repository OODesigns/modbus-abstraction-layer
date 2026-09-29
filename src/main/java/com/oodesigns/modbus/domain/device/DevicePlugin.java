package com.oodesigns.modbus.domain.device;

import com.oodesigns.modbus.domain.Response;
import com.oodesigns.modbus.domain.value.DependencyKey;
import com.oodesigns.modbus.domain.value.DeviceType;
import java.util.Set;

public interface DevicePlugin {
    DeviceType deviceType();

    Set<DependencyKey> requiredDependencies();

    Response<Device> create(ConfigLoader config, Dependencies dependencies);
}