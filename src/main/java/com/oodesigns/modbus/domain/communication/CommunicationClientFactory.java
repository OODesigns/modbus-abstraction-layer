package com.oodesigns.modbus.domain.communication;

import com.oodesigns.modbus.domain.Response;
import com.oodesigns.modbus.domain.connection.ConnectionSettings;

public interface CommunicationClientFactory {
    TransportKey transport();

    Response<CommunicationClient> create(ConnectionSettings settings);
}