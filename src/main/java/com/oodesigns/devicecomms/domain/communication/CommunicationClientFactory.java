package com.oodesigns.devicecomms.domain.communication;

import com.oodesigns.devicecomms.domain.Response;
import com.oodesigns.devicecomms.domain.connection.ConnectionSettings;

public interface CommunicationClientFactory {
    TransportKey transport();

    Response<CommunicationClient> create(ConnectionSettings settings);
}