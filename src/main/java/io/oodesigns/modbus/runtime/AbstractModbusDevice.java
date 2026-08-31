package io.oodesigns.modbus.runtime;
import io.oodesigns.modbus.*; import io.oodesigns.modbus.client.ModbusClient; import io.oodesigns.modbus.plugin.*; import java.util.*; import java.util.concurrent.*;
/** Base lifecycle: connect, mark running, and read once. Start failures stop the device and record START_FAILURE. */
public abstract class AbstractModbusDevice implements Device {
 protected final ModbusClient client; protected final StateManager stateManager;
 private boolean running;
 protected AbstractModbusDevice(ModbusClient client, StateManager stateManager){this.client=Objects.requireNonNull(client);this.stateManager=Objects.requireNonNull(stateManager);}
 public CompletableFuture<Response<Void>> open(){try{return client.connect().handle((r,e)->e==null?r:Response.<Void>failure(e.getMessage())).thenApply(result->{if(result.status()==Status.OK){running=true;stateManager.updateState(new DeviceState(Map.of("running",true)),Set.of());return result;}running=false;stateManager.updateState(new DeviceState(Map.of("running",false)),Set.of("START_FAILURE"));return result;});}catch(RuntimeException e){running=false;stateManager.updateState(new DeviceState(Map.of("running",false)),Set.of("START_FAILURE"));return CompletableFuture.completedFuture(Response.failure(e.getMessage()));}}
 public CompletableFuture<Response<DeviceState>> read(){if(!running)return CompletableFuture.completedFuture(Response.failure("DEVICE_STOPPED"));try{return readData().handle((r,e)->e==null?r:Response.<DeviceState>failure(e.getMessage()));}catch(RuntimeException e){return CompletableFuture.completedFuture(Response.failure(e.getMessage()));}}
 public CompletableFuture<Response<Void>> close(){running=false;return client.disconnect().handle((r,e)->e==null?r:Response.<Void>failure(e.getMessage()));}
 protected abstract CompletableFuture<Response<DeviceState>> readData();
}
