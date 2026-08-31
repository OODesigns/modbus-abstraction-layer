package io.oodesigns.modbus.plugin;
import io.oodesigns.modbus.Response; import io.oodesigns.modbus.value.*; import java.util.*; import java.util.concurrent.*;
/** Minimal ServiceLoader device used to verify plugin discovery without hardware. */
public final class InMemoryDevicePlugin implements DevicePlugin {
 private static final DeviceType TYPE=new DeviceType("in-memory");
 public DeviceType deviceType(){return TYPE;} public Set<DependencyKey> requiredDependencies(){return Set.of();}
 public Response<Device> create(ConfigLoader config, Dependencies dependencies){return Response.success(new Device(){public CompletableFuture<Response<Void>> open(){return CompletableFuture.completedFuture(Response.success(null));}public CompletableFuture<Response<DeviceState>> read(){return CompletableFuture.completedFuture(Response.success(new DeviceState(Map.of())));}public CompletableFuture<Response<Void>> close(){return CompletableFuture.completedFuture(Response.success(null));}});}
}
