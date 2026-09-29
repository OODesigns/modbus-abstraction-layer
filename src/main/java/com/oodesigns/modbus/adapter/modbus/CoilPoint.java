package com.oodesigns.modbus.adapter.modbus;

import com.oodesigns.modbus.domain.value.StartAddress;
import java.util.Objects;

public record CoilPoint(StartAddress address) implements ModbusPointAddress<Boolean> {
    public CoilPoint {
        Objects.requireNonNull(address, "address");
    }
}