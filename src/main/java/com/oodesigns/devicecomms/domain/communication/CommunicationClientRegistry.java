package com.oodesigns.devicecomms.domain.communication;

import com.oodesigns.devicecomms.domain.Response;
import com.oodesigns.devicecomms.domain.connection.ConnectionSettings;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class CommunicationClientRegistry {
    private final Map<TransportKey, CommunicationClientFactory> factories;

    public CommunicationClientRegistry(final Collection<CommunicationClientFactory> factories) {
        Objects.requireNonNull(factories, "factories");
        final Map<TransportKey, CommunicationClientFactory> indexed = new HashMap<>();
        for (final CommunicationClientFactory factory : factories) {
            Objects.requireNonNull(factory, "factory");
            final TransportKey transport = Objects.requireNonNull(factory.transport(), "factory transport");
            if (indexed.putIfAbsent(transport, factory) != null) {
                throw new IllegalArgumentException("duplicate communication transport: " + transport.value());
            }
        }
        this.factories = Map.copyOf(indexed);
    }

    public CommunicationClientRegistry(final ProviderCatalog catalog) {
        this(Objects.requireNonNull(catalog, "catalog").communicationClientFactories());
    }

    public Response<CommunicationClient> get(final TransportKey transport, final ConnectionSettings settings) {
        Objects.requireNonNull(transport, "transport");
        Objects.requireNonNull(settings, "settings");
        final CommunicationClientFactory factory = factories.get(transport);
        if (factory == null) {
            return Response.failure("communication transport is not registered: " + transport.value());
        }
        try {
            return Objects.requireNonNull(factory.create(settings), "factory result");
        } catch (final RuntimeException exception) {
            return Response.failure("communication client creation failed: " + exception.getMessage());
        }
    }
}