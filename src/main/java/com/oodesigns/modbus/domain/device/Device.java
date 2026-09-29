package com.oodesigns.modbus.domain.device;

import com.oodesigns.modbus.domain.Response;
import java.util.concurrent.CompletableFuture;

public interface Device {
    CompletableFuture<Response<Void>> open();

    CompletableFuture<Response<DeviceState>> read(DeviceQuery query);

    CompletableFuture<Response<Void>> execute(DeviceCommand command);

    CompletableFuture<Response<Void>> close();
}