package io.oodesigns.modbus.client;
import io.oodesigns.modbus.Response;
import io.oodesigns.modbus.value.*;
import java.util.concurrent.CompletableFuture;
/** Intent-focused, asynchronous Modbus transport contract. Futures always complete normally. */
public interface ModbusClient {
 CompletableFuture<Response<Void>> connect();
 CompletableFuture<Response<Void>> disconnect();
 CompletableFuture<Response<boolean[]>> readCoils(StartAddress start, CoilCount count);
 CompletableFuture<Response<boolean[]>> readDiscreteInputs(StartAddress start, CoilCount count);
 CompletableFuture<Response<int[]>> readHoldingRegisters(StartAddress start, RegisterCount count);
 CompletableFuture<Response<int[]>> readInputRegisters(StartAddress start, RegisterCount count);
 CompletableFuture<Response<Void>> writeCoil(StartAddress address, boolean value);
 CompletableFuture<Response<Void>> writeRegister(StartAddress address, int value);
 Response<Boolean> isConnected();
}
