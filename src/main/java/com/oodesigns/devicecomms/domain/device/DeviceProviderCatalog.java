package com.oodesigns.devicecomms.domain.device;

import java.util.Collection;

public interface DeviceProviderCatalog {
    Collection<DevicePlugin> devicePlugins();
}