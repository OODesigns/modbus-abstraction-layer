package com.oodesigns.devicecomms.adapter.modbus;

import com.oodesigns.devicecomms.domain.value.StartAddress;
import java.util.Objects;

public record HoldingRegisterPoint(StartAddress address) implements ModbusPointAddress<Integer> {
    public HoldingRegisterPoint {
        Objects.requireNonNull(address, "address");
    }
}