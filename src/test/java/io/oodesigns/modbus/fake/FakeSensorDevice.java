package io.oodesigns.modbus.fake;

import io.oodesigns.modbus.client.ModbusClient;
import io.oodesigns.modbus.core.Response;
import io.oodesigns.modbus.device.Device;
import io.oodesigns.modbus.device.DeviceState;
import io.oodesigns.modbus.value.RegisterCount;
import io.oodesigns.modbus.value.StartAddress;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/** Minimal {@link Device} produced by {@link FakeTemperatureSensorPlugin}. */
public final class FakeSensorDevice implements Device {

    private final ModbusClient client;

    public FakeSensorDevice(ModbusClient client) {
        if (client == null) {
            throw new IllegalArgumentException("client is required");
        }
        this.client = client;
    }

    @Override
    public CompletableFuture<Response<Void>> open() {
        return client.connect();
    }

    @Override
    public CompletableFuture<Response<DeviceState>> read() {
        return client.readHoldingRegisters(new StartAddress(0), new RegisterCount(1))
                .thenApply(response -> response.map(registers -> DeviceState.operational(
                        Map.of("raw", registers.at(0).map(Object.class::cast).orElse("none")))));
    }

    @Override
    public Response<Void> close() {
        return client.disconnect();
    }
}
