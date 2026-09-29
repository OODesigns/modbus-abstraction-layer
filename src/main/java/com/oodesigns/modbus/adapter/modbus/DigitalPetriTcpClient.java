package com.oodesigns.modbus.adapter.modbus;

import com.digitalpetri.modbus.client.ModbusTcpClient;
import com.digitalpetri.modbus.tcp.client.NettyClientTransportConfig;
import com.digitalpetri.modbus.tcp.client.NettyTcpClientTransport;
import com.oodesigns.modbus.domain.connection.NetworkEndpoint;
import com.oodesigns.modbus.domain.value.UnitId;
import java.util.Objects;

public final class DigitalPetriTcpClient extends AbstractDigitalPetriClient {
    public DigitalPetriTcpClient(final NetworkEndpoint endpoint) {
        this(endpoint, new UnitId(1));
    }

    public DigitalPetriTcpClient(final NetworkEndpoint endpoint, final UnitId unitId) {
        super(createClient(endpoint), Objects.requireNonNull(unitId, "unitId"));
    }

    private static ModbusTcpClient createClient(final NetworkEndpoint endpoint) {
        final NettyClientTransportConfig config = NettyClientTransportConfig.create(builder -> {
            builder.hostname = endpoint.host().value();
            builder.port = endpoint.port().value();
            builder.connectTimeout = endpoint.timeout().value();
        });
        return ModbusTcpClient.create(new NettyTcpClientTransport(config),
            builder -> builder.requestTimeout = endpoint.timeout().value());
    }
}