package com.oodesigns.modbus.testkit;

import com.oodesigns.modbus.domain.Response;
import com.oodesigns.modbus.domain.communication.CommunicationClient;
import com.oodesigns.modbus.domain.communication.CommunicationClientFactory;
import com.oodesigns.modbus.domain.communication.TransportKey;
import com.oodesigns.modbus.domain.connection.ConnectionSettings;

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