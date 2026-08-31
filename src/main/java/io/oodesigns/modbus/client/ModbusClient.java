package io.oodesigns.modbus.client;

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

/**
 * The only Modbus API the rest of the system sees.
 *
 * <p>Devices never touch an underlying Modbus library directly; they speak this vocabulary of
 * validated value objects. No method throws: asynchronous operations always complete normally,
 * carrying either a successful {@link Response} or a failure describing what went wrong.</p>
 */
public interface ModbusClient {

    /**
     * Opens the connection to the device.
     *
     * @return a future that always completes normally, successfully when connected
     */
    CompletableFuture<Response<Void>> connect();

    /**
     * Closes the connection, releasing any transport resources.
     *
     * @return a success response once the connection is closed, a failure describing why not
     */
    Response<Void> disconnect();

    /**
     * @param start first coil to read
     * @param count how many coils to read
     * @return a future carrying the coils, or a failure response
     */
    CompletableFuture<Response<CoilValues>> readCoils(StartAddress start, CoilCount count);

    /**
     * @param start first discrete input to read
     * @param count how many inputs to read
     * @return a future carrying the inputs, or a failure response
     */
    CompletableFuture<Response<CoilValues>> readDiscreteInputs(StartAddress start, CoilCount count);

    /**
     * @param start first holding register to read
     * @param count how many registers to read
     * @return a future carrying the registers, or a failure response
     */
    CompletableFuture<Response<RegisterValues>> readHoldingRegisters(StartAddress start, RegisterCount count);

    /**
     * @param start first input register to read
     * @param count how many registers to read
     * @return a future carrying the registers, or a failure response
     */
    CompletableFuture<Response<RegisterValues>> readInputRegisters(StartAddress start, RegisterCount count);

    /**
     * @param address the coil to write
     * @param value the state to write
     * @return a future carrying a success response once written, or a failure response
     */
    CompletableFuture<Response<Void>> writeCoil(CoilAddress address, CoilValue value);

    /**
     * @param address the register to write
     * @param value the content to write
     * @return a future carrying a success response once written, or a failure response
     */
    CompletableFuture<Response<Void>> writeRegister(RegisterAddress address, RegisterValue value);

    /** @return {@code true} while the client holds an open connection */
    boolean isConnected();
}
