package com.oodesigns.devicecomms.adapter.modbus;

import com.oodesigns.devicecomms.domain.Response;
import com.oodesigns.devicecomms.domain.communication.CommunicationClient;
import com.oodesigns.devicecomms.domain.communication.CommunicationClientFactory;
import com.oodesigns.devicecomms.domain.communication.ConnectionManager;
import com.oodesigns.devicecomms.domain.communication.TransportKey;
import com.oodesigns.devicecomms.domain.connection.ConnectionSettings;
import com.oodesigns.devicecomms.domain.connection.NetworkEndpoint;
import com.oodesigns.devicecomms.domain.value.Retries;
import com.oodesigns.devicecomms.domain.value.UnitId;
import java.util.Objects;

public final class ModbusTcpClientFactory implements CommunicationClientFactory {
    private final UnitId unitId;

    public ModbusTcpClientFactory() {
        this(new UnitId(1));
    }

    public ModbusTcpClientFactory(final UnitId unitId) {
        this.unitId = Objects.requireNonNull(unitId, "unitId");
    }

    @Override
    public TransportKey transport() {
        return ModbusTransportKeys.TCP;
    }

    @Override
    public Response<CommunicationClient> create(final ConnectionSettings settings) {
        if (!(settings instanceof NetworkEndpoint endpoint)) {
            return Response.failure("Modbus TCP requires network endpoint settings");
        }
        try {
            return Response.success(new ConnectionManager(new DigitalPetriTcpClient(endpoint, unitId),
                    new Retries(2), endpoint.timeout()));
        } catch (final RuntimeException exception) {
            return Response.failure("Modbus TCP client could not be created: " + detail(exception));
        }
    }

    private static String detail(final RuntimeException exception) {
        return exception.getMessage() == null || exception.getMessage().isBlank()
                ? "transport configuration is invalid"
                : exception.getMessage();
    }
}