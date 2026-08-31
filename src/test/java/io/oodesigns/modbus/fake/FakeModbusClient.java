package io.oodesigns.modbus.fake;

import io.oodesigns.modbus.client.ModbusClient;
import io.oodesigns.modbus.core.Response;
import io.oodesigns.modbus.value.CoilAddress;
import io.oodesigns.modbus.value.CoilCount;
import io.oodesigns.modbus.value.CoilValue;
import io.oodesigns.modbus.value.CoilValues;
import io.oodesigns.modbus.value.RegisterAddress;
import io.oodesigns.modbus.value.RegisterCount;
import io.oodesigns.modbus.value.RegisterValue;
import io.oodesigns.modbus.value.RegisterValues;
import io.oodesigns.modbus.value.StartAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * In-memory {@link ModbusClient} used by tests. It never throws and can be scripted to fail a
 * given number of times before succeeding, or to never answer at all.
 */
public final class FakeModbusClient implements ModbusClient {

    private final AtomicInteger connectAttempts = new AtomicInteger();
    private final AtomicInteger readAttempts = new AtomicInteger();
    private final AtomicInteger disconnects = new AtomicInteger();

    private int failedConnectsRemaining;
    private int failedReadsRemaining;
    private boolean connected;
    private boolean neverAnswers;
    private int[] registers = {0};

    public FakeModbusClient failConnects(int times) {
        this.failedConnectsRemaining = times;
        return this;
    }

    public FakeModbusClient failReads(int times) {
        this.failedReadsRemaining = times;
        return this;
    }

    public FakeModbusClient neverAnswers() {
        this.neverAnswers = true;
        return this;
    }

    public FakeModbusClient withRegisters(int... values) {
        this.registers = values.clone();
        return this;
    }

    public FakeModbusClient dropConnection() {
        this.connected = false;
        return this;
    }

    public int connectAttempts() {
        return connectAttempts.get();
    }

    public int readAttempts() {
        return readAttempts.get();
    }

    public int disconnects() {
        return disconnects.get();
    }

    @Override
    public CompletableFuture<Response<Void>> connect() {
        connectAttempts.incrementAndGet();
        if (neverAnswers) {
            return new CompletableFuture<>();
        }
        if (failedConnectsRemaining > 0) {
            failedConnectsRemaining--;
            return CompletableFuture.completedFuture(Response.failure("connect refused"));
        }
        connected = true;
        return CompletableFuture.completedFuture(Response.success());
    }

    @Override
    public Response<Void> disconnect() {
        disconnects.incrementAndGet();
        connected = false;
        return Response.success();
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public CompletableFuture<Response<CoilValues>> readCoils(StartAddress start, CoilCount count) {
        boolean[] coils = new boolean[count.value()];
        return CompletableFuture.completedFuture(Response.success(new CoilValues(coils)));
    }

    @Override
    public CompletableFuture<Response<CoilValues>> readDiscreteInputs(StartAddress start, CoilCount count) {
        return readCoils(start, count);
    }

    @Override
    public CompletableFuture<Response<RegisterValues>> readHoldingRegisters(StartAddress start, RegisterCount count) {
        readAttempts.incrementAndGet();
        if (neverAnswers) {
            return new CompletableFuture<>();
        }
        if (!connected) {
            return CompletableFuture.completedFuture(Response.failure("not connected"));
        }
        if (failedReadsRemaining > 0) {
            failedReadsRemaining--;
            return CompletableFuture.completedFuture(Response.failure("Modbus exception code 2"));
        }
        return CompletableFuture.completedFuture(Response.success(new RegisterValues(registers)));
    }

    @Override
    public CompletableFuture<Response<RegisterValues>> readInputRegisters(StartAddress start, RegisterCount count) {
        return readHoldingRegisters(start, count);
    }

    @Override
    public CompletableFuture<Response<Void>> writeCoil(CoilAddress address, CoilValue value) {
        return CompletableFuture.completedFuture(Response.success());
    }

    @Override
    public CompletableFuture<Response<Void>> writeRegister(RegisterAddress address, RegisterValue value) {
        return CompletableFuture.completedFuture(Response.success());
    }
}
