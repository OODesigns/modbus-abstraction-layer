package com.oodesigns.modbus.adapter.modbus;

import com.oodesigns.modbus.domain.Response;
import com.oodesigns.modbus.domain.communication.CommunicationClient;
import com.oodesigns.modbus.domain.communication.CommunicationClientFactory;
import com.oodesigns.modbus.domain.communication.ConnectionManager;
import com.oodesigns.modbus.domain.communication.TransportKey;
import com.oodesigns.modbus.domain.connection.ConnectionSettings;
import com.oodesigns.modbus.domain.connection.SerialEndpoint;
import com.oodesigns.modbus.domain.value.Retries;
import com.oodesigns.modbus.domain.value.UnitId;
import java.util.Objects;

public final class ModbusRtuClientFactory implements CommunicationClientFactory {
    private final UnitId unitId;

    public ModbusRtuClientFactory() {
        this(new UnitId(1));
    }

    public ModbusRtuClientFactory(final UnitId unitId) {
        this.unitId = Objects.requireNonNull(unitId, "unitId");
    }

    @Override
    public TransportKey transport() {
        return ModbusTransportKeys.RTU;
    }

    @Override
    public Response<CommunicationClient> create(final ConnectionSettings settings) {
        if (!(settings instanceof SerialEndpoint endpoint)) {
            return Response.failure("Modbus RTU requires serial endpoint settings");
        }
        try {
            return Response.success(new ConnectionManager(new DigitalPetriRtuClient(endpoint, unitId),
                    new Retries(2), endpoint.timeout()));
        } catch (final RuntimeException exception) {
            return Response.failure("Modbus RTU client could not be created: " + detail(exception));
        }
    }

    private static String detail(final RuntimeException exception) {
        return exception.getMessage() == null || exception.getMessage().isBlank()
                ? "transport configuration is invalid"
                : exception.getMessage();
    }
}