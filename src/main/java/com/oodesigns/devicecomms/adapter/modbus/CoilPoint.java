package com.oodesigns.devicecomms.adapter.modbus;

import com.oodesigns.devicecomms.domain.value.StartAddress;
import java.util.Objects;

public record CoilPoint(StartAddress address) implements ModbusPointAddress<Boolean> {
    public CoilPoint {
        Objects.requireNonNull(address, "address");
    }
}