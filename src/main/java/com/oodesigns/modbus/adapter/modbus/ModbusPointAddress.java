package com.oodesigns.modbus.adapter.modbus;

import com.oodesigns.modbus.domain.communication.PointAddress;
import com.oodesigns.modbus.domain.value.StartAddress;

public sealed interface ModbusPointAddress<T> extends PointAddress<T>
        permits CoilPoint, DiscreteInputPoint, HoldingRegisterPoint, InputRegisterPoint {
    StartAddress address();
}