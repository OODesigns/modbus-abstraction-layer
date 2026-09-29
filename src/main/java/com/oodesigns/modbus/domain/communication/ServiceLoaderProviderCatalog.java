package com.oodesigns.modbus.domain.communication;

import java.util.Collection;
import java.util.ServiceLoader;

public final class ServiceLoaderProviderCatalog implements ProviderCatalog {
    @Override
    public Collection<CommunicationClientFactory> communicationClientFactories() {
        return ServiceLoader.load(CommunicationClientFactory.class).stream()
                .map(ServiceLoader.Provider::get)
                .toList();
    }
}