package com.oodesigns.modbus.adapter.modbus;

import com.oodesigns.modbus.domain.value.StartAddress;
import java.util.Objects;

public record DiscreteInputPoint(StartAddress address) implements ModbusPointAddress<Boolean> {
    public DiscreteInputPoint {
        Objects.requireNonNull(address, "address");
    }
}