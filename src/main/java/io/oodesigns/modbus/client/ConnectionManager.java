package io.oodesigns.modbus.client;
import io.oodesigns.modbus.Response;
import io.oodesigns.modbus.value.*;
import java.util.Objects;
import java.util.concurrent.*;
/** Retry decorator. It reconnects on failed connection attempts up to the configured retry count. */
public final class ConnectionManager implements ModbusClient {
 private final ModbusClient delegate; private final Retries retries;
 public ConnectionManager(ModbusClient delegate, Retries retries) { this.delegate=Objects.requireNonNull(delegate); this.retries=Objects.requireNonNull(retries); }
 public CompletableFuture<Response<Void>> connect() { return attempt(0); }
 private CompletableFuture<Response<Void>> attempt(int number) {
  try { return delegate.connect().handle((response, error) -> error == null ? response : Response.<Void>failure(error.getMessage())).thenCompose(response -> response.status().equals(io.oodesigns.modbus.Status.OK) || number >= retries.value() ? CompletableFuture.completedFuture(response) : safelyDisconnect().thenCompose(ignored -> attempt(number + 1))); }
  catch (RuntimeException exception) { return CompletableFuture.completedFuture(Response.failure(exception.getMessage())); }
 }
 private CompletableFuture<Response<Void>> safelyDisconnect() { try { return delegate.disconnect().exceptionally(error -> Response.failure(error.getMessage())); } catch (RuntimeException error) { return CompletableFuture.completedFuture(Response.failure(error.getMessage())); } }
 public CompletableFuture<Response<Void>> disconnect() { return safelyDisconnect(); }
 public CompletableFuture<Response<boolean[]>> readCoils(StartAddress start, CoilCount count) { return safe(() -> delegate.readCoils(start,count)); }
 public CompletableFuture<Response<boolean[]>> readDiscreteInputs(StartAddress start, CoilCount count) { return safe(() -> delegate.readDiscreteInputs(start,count)); }
 public CompletableFuture<Response<int[]>> readHoldingRegisters(StartAddress start, RegisterCount count) { return safe(() -> delegate.readHoldingRegisters(start,count)); }
 public CompletableFuture<Response<int[]>> readInputRegisters(StartAddress start, RegisterCount count) { return safe(() -> delegate.readInputRegisters(start,count)); }
 public CompletableFuture<Response<Void>> writeCoil(StartAddress address, boolean value) { return safe(() -> delegate.writeCoil(address,value)); }
 public CompletableFuture<Response<Void>> writeRegister(StartAddress address, int value) { return safe(() -> delegate.writeRegister(address,value)); }
 public Response<Boolean> isConnected() { try { return delegate.isConnected(); } catch (RuntimeException e) { return Response.failure(e.getMessage()); } }
 private static <T> CompletableFuture<Response<T>> safe(java.util.function.Supplier<CompletableFuture<Response<T>>> action) { try { return action.get().handle((response,error) -> error == null ? response : Response.failure(error.getMessage())); } catch (RuntimeException e) { return CompletableFuture.completedFuture(Response.failure(e.getMessage())); } }
}
