package com.oodesigns.devicecomms.testkit;

import com.oodesigns.devicecomms.domain.Response;
import com.oodesigns.devicecomms.domain.communication.CommunicationClient;
import com.oodesigns.devicecomms.domain.communication.CommunicationClientFactory;
import com.oodesigns.devicecomms.domain.communication.TransportKey;
import com.oodesigns.devicecomms.domain.connection.ConnectionSettings;

public final class TestCommunicationClientFactory implements CommunicationClientFactory {
    public static final TransportKey TRANSPORT = new TransportKey("test-memory");

    @Override
    public TransportKey transport() {
        return TRANSPORT;
    }

    @Override
    public Response<CommunicationClient> create(final ConnectionSettings settings) {
        return Response.success(new InMemoryCommunicationClient());
    }
}