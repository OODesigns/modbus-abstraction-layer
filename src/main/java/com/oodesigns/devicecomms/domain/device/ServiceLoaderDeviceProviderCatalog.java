package com.oodesigns.devicecomms.domain.device;

import java.util.Collection;
import java.util.ServiceLoader;

public final class ServiceLoaderDeviceProviderCatalog implements DeviceProviderCatalog {
    @Override
    public Collection<DevicePlugin> devicePlugins() {
        return ServiceLoader.load(DevicePlugin.class).stream()
                .map(ServiceLoader.Provider::get)
                .toList();
    }
}