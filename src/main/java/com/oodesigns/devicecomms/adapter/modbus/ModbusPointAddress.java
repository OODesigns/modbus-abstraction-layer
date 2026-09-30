package com.oodesigns.devicecomms.adapter.modbus;

import com.oodesigns.devicecomms.domain.communication.PointAddress;
import com.oodesigns.devicecomms.domain.value.StartAddress;

public sealed interface ModbusPointAddress<T> extends PointAddress<T>
        permits CoilPoint, DiscreteInputPoint, HoldingRegisterPoint, InputRegisterPoint {
    StartAddress address();
}