package com.oodesigns.modbus.adapter.modbus;

import com.digitalpetri.modbus.client.ModbusRtuClient;
import com.digitalpetri.modbus.serial.SerialPortTransportConfig;
import com.digitalpetri.modbus.serial.client.SerialPortClientTransport;
import com.oodesigns.modbus.domain.connection.SerialEndpoint;
import com.oodesigns.modbus.domain.value.UnitId;
import java.util.Objects;

public final class DigitalPetriRtuClient extends AbstractDigitalPetriClient {
    public DigitalPetriRtuClient(final SerialEndpoint endpoint) {
        this(endpoint, new UnitId(1));
    }

    public DigitalPetriRtuClient(final SerialEndpoint endpoint, final UnitId unitId) {
        super(createClient(endpoint), Objects.requireNonNull(unitId, "unitId"));
    }

    private static ModbusRtuClient createClient(final SerialEndpoint endpoint) {
        final SerialPortTransportConfig config = SerialPortTransportConfig.create(builder -> {
            builder.serialPort = endpoint.port().value();
            builder.baudRate = endpoint.baudRate().value();
        });
        return ModbusRtuClient.create(new SerialPortClientTransport(config),
            builder -> builder.requestTimeout = endpoint.timeout().value());
    }
}