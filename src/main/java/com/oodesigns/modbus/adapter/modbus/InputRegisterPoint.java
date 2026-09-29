package com.oodesigns.modbus.adapter.modbus;

import com.oodesigns.modbus.domain.value.StartAddress;
import java.util.Objects;

public record InputRegisterPoint(StartAddress address) implements ModbusPointAddress<Integer> {
    public InputRegisterPoint {
        Objects.requireNonNull(address, "address");
    }
}