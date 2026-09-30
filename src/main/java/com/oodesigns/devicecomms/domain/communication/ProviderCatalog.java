package com.oodesigns.devicecomms.domain.communication;

import java.util.Collection;

public interface ProviderCatalog {
    Collection<CommunicationClientFactory> communicationClientFactories();
}