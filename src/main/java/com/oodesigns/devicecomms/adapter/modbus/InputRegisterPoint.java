package com.oodesigns.devicecomms.adapter.modbus;

import com.oodesigns.devicecomms.domain.value.StartAddress;
import java.util.Objects;

public record InputRegisterPoint(StartAddress address) implements ModbusPointAddress<Integer> {
    public InputRegisterPoint {
        Objects.requireNonNull(address, "address");
    }
}