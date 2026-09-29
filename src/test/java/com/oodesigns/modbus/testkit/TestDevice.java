package com.oodesigns.modbus.testkit;

import com.oodesigns.modbus.domain.Response;
import com.oodesigns.modbus.domain.device.ConfigLoader;
import com.oodesigns.modbus.domain.device.Device;
import com.oodesigns.modbus.domain.device.DeviceCommand;
import com.oodesigns.modbus.domain.device.DeviceQuery;
import com.oodesigns.modbus.domain.device.DeviceState;
import java.util.concurrent.CompletableFuture;

public final class TestDevice implements Device {
    private record TestState(java.util.Map<String, Object> values) implements DeviceState {
    }

    private final ConfigLoader config;

    public TestDevice(final ConfigLoader config) {
        this.config = config;
    }

    @Override
    public CompletableFuture<Response<Void>> open() {
        return CompletableFuture.completedFuture(Response.success());
    }

    @Override
    public CompletableFuture<Response<DeviceState>> read(final DeviceQuery query) {
        return CompletableFuture.completedFuture(Response.success(new TestState(config.values())));
    }

    @Override
    public CompletableFuture<Response<Void>> execute(final DeviceCommand command) {
        return CompletableFuture.completedFuture(Response.success());
    }

    @Override
    public CompletableFuture<Response<Void>> close() {
        return CompletableFuture.completedFuture(Response.success());
    }
}