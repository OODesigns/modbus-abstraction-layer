package io.oodesigns.modbus.plugin;
import io.oodesigns.modbus.Response; import java.util.concurrent.CompletableFuture;
public interface Device { CompletableFuture<Response<Void>> open(); CompletableFuture<Response<DeviceState>> read(); CompletableFuture<Response<Void>> close(); }
