package io.oodesigns.modbus.fake;

import io.oodesigns.modbus.client.ConnectionSettings;
import io.oodesigns.modbus.client.ModbusClient;
import io.oodesigns.modbus.client.ModbusClientFactory;
import io.oodesigns.modbus.client.TransportType;
import io.oodesigns.modbus.core.Response;

/** Fake TCP binding registered through {@code META-INF/services} for the tests. */
public final class FakeTcpClientFactory implements ModbusClientFactory {

    @Override
    public TransportType transport() {
        return TransportType.TCP;
    }

    @Override
    public Response<ModbusClient> create(ConnectionSettings settings) {
        return settings.host()
                .map(host -> Response.<ModbusClient>success(new FakeModbusClient()))
                .orElseGet(() -> Response.failure("TCP settings require a host"));
    }
}
