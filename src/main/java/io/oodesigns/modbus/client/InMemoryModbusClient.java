package io.oodesigns.modbus.client;
import io.oodesigns.modbus.Response; import io.oodesigns.modbus.value.*; import java.util.concurrent.*;
/** Non-network adapter for development and ServiceLoader integration tests. */
public final class InMemoryModbusClient implements ModbusClient {
 private boolean connected;
 public CompletableFuture<Response<Void>> connect() { connected=true; return CompletableFuture.completedFuture(Response.success(nullValue())); }
 public CompletableFuture<Response<Void>> disconnect() { connected=false; return CompletableFuture.completedFuture(Response.success(nullValue())); }
 public CompletableFuture<Response<boolean[]>> readCoils(StartAddress s, CoilCount c) { return CompletableFuture.completedFuture(Response.success(new boolean[c.value()])); }
 public CompletableFuture<Response<boolean[]>> readDiscreteInputs(StartAddress s, CoilCount c) { return readCoils(s,c); }
 public CompletableFuture<Response<int[]>> readHoldingRegisters(StartAddress s, RegisterCount c) { return CompletableFuture.completedFuture(Response.success(new int[c.value()])); }
 public CompletableFuture<Response<int[]>> readInputRegisters(StartAddress s, RegisterCount c) { return readHoldingRegisters(s,c); }
 public CompletableFuture<Response<Void>> writeCoil(StartAddress a, boolean v) { return CompletableFuture.completedFuture(Response.success(nullValue())); }
 public CompletableFuture<Response<Void>> writeRegister(StartAddress a, int v) { return CompletableFuture.completedFuture(Response.success(nullValue())); }
 public Response<Boolean> isConnected() { return Response.success(connected); }
 private static Void nullValue() { return null; }
}
