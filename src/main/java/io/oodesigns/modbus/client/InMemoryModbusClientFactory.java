package io.oodesigns.modbus.client;
import io.oodesigns.modbus.Response;
public final class InMemoryModbusClientFactory implements ModbusClientFactory { public TransportType transport(){return TransportType.TCP;} public Response<ModbusClient> create(ConnectionSettings settings){return Response.success(new InMemoryModbusClient());} }
