package com.oodesigns.modbus.domain.communication;

import java.util.Collection;

public interface ProviderCatalog {
    Collection<CommunicationClientFactory> communicationClientFactories();
}