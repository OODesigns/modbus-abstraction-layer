package com.oodesigns.modbus.adapter.modbus;

import com.oodesigns.modbus.domain.value.StartAddress;
import java.util.Objects;

public record HoldingRegisterPoint(StartAddress address) implements ModbusPointAddress<Integer> {
    public HoldingRegisterPoint {
        Objects.requireNonNull(address, "address");
    }
}