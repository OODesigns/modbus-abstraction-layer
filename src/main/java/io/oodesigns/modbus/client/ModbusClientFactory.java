package io.oodesigns.modbus.client;
import io.oodesigns.modbus.Response;
public interface ModbusClientFactory { TransportType transport(); Response<ModbusClient> create(ConnectionSettings settings); }
