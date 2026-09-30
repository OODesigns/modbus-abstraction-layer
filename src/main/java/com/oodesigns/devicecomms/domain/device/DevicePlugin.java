package com.oodesigns.devicecomms.domain.device;

import com.oodesigns.devicecomms.domain.Response;
import com.oodesigns.devicecomms.domain.value.DependencyKey;
import com.oodesigns.devicecomms.domain.value.DeviceType;
import java.util.Set;

public interface DevicePlugin {
    DeviceType deviceType();

    Set<DependencyKey> requiredDependencies();

    Response<Device> create(ConfigLoader config, Dependencies dependencies);
}