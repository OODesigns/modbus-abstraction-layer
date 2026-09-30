package com.oodesigns.devicecomms.device.blauberg.command;

import com.oodesigns.devicecomms.adapter.modbus.CoilPoint;
import com.oodesigns.devicecomms.domain.value.StartAddress;

public enum Sensor {
    INTERNAL_RH(5),
    EXTERNAL_RH(6),
    INTERNAL_CO2(7),
    EXTERNAL_CO2(8),
    INTERNAL_PM25(9),
    EXTERNAL_PM25(10),
    INTERNAL_VOC(11),
    EXTERNAL_VOC(12);

    private final CoilPoint controlPoint;

    Sensor(final int address) {
        controlPoint = new CoilPoint(new StartAddress(address));
    }

    public CoilPoint controlPoint() {
        return controlPoint;
    }
}