package com.oodesigns.devicecomms.domain.device;

import com.oodesigns.devicecomms.domain.Response;
import com.oodesigns.devicecomms.domain.value.DeviceType;

public interface ConfigFactory {
    Response<ConfigLoader> load(DeviceType deviceType);
}