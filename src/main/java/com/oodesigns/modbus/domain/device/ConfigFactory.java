package com.oodesigns.modbus.domain.device;

import com.oodesigns.modbus.domain.Response;
import com.oodesigns.modbus.domain.value.DeviceType;

public interface ConfigFactory {
    Response<ConfigLoader> load(DeviceType deviceType);
}