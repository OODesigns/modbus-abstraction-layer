package com.oodesigns.devicecomms.domain.communication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import com.oodesigns.devicecomms.domain.Response;
import com.oodesigns.devicecomms.domain.connection.NetworkEndpoint;
import com.oodesigns.devicecomms.domain.value.IPAddress;
import com.oodesigns.devicecomms.domain.value.Port;
import com.oodesigns.devicecomms.domain.value.Timeout;
import com.oodesigns.devicecomms.testkit.InMemoryCommunicationClient;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.Test;

class CommunicationClientRegistryTest {
    @Test
    void resolvesRegisteredProviderFromCatalog() {
        final CommunicationClientRegistry registry = new CommunicationClientRegistry(
                new ServiceLoaderProviderCatalog());
        final var settings = new NetworkEndpoint(new IPAddress("127.0.0.1"), new Port(502),
                new Timeout(Duration.ofSeconds(1)));

        final var response = registry.get(new TransportKey("test-memory"), settings);

        assertEquals(Response.Status.OK, response.status());
        assertInstanceOf(InMemoryCommunicationClient.class, response.value());
    }

    @Test
    void returnsFailureForUnregisteredTransport() {
        final CommunicationClientRegistry registry = new CommunicationClientRegistry(List.of());

        final var response = registry.get(new TransportKey("missing"),
                new NetworkEndpoint(new IPAddress("127.0.0.1"), new Port(502),
                        new Timeout(Duration.ofSeconds(1))));

        assertEquals(Response.Status.EXCEPTION, response.status());
    }
}