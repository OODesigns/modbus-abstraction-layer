package com.oodesigns.devicecomms.device.blauberg.value;

import com.oodesigns.devicecomms.adapter.modbus.HoldingRegisterPoint;
import com.oodesigns.devicecomms.domain.value.StartAddress;

public enum FanPreset {
    STANDBY(25),
    SPEED_1(27),
    SPEED_2(29),
    SPEED_3(31),
    SPEED_4(33),
    SPEED_5(35);

    private final HoldingRegisterPoint supplyFlowPoint;
    private final HoldingRegisterPoint extractFlowPoint;

    FanPreset(final int supplyAddress) {
        supplyFlowPoint = new HoldingRegisterPoint(new StartAddress(supplyAddress));
        extractFlowPoint = new HoldingRegisterPoint(new StartAddress(supplyAddress + 1));
    }

    public HoldingRegisterPoint supplyFlowPoint() {
        return supplyFlowPoint;
    }

    public HoldingRegisterPoint extractFlowPoint() {
        return extractFlowPoint;
    }
}