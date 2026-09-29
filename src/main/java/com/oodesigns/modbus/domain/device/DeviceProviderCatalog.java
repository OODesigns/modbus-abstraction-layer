package com.oodesigns.modbus.domain.device;

import java.util.Collection;

public interface DeviceProviderCatalog {
    Collection<DevicePlugin> devicePlugins();
}