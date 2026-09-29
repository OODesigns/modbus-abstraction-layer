package com.oodesigns.modbus.adapter.modbus;

import com.oodesigns.modbus.domain.communication.TransportKey;

public final class ModbusTransportKeys {
    public static final TransportKey TCP = new TransportKey("modbus-tcp");
    public static final TransportKey RTU = new TransportKey("modbus-rtu");

    private ModbusTransportKeys() {
    }
}