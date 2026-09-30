package com.oodesigns.devicecomms.device.blauberg.value;

import com.oodesigns.devicecomms.adapter.modbus.HoldingRegisterPoint;
import com.oodesigns.devicecomms.domain.value.StartAddress;
import java.util.Objects;

public record ScheduleSlot(ScheduleDay day, SchedulePeriodNumber period) {
    public ScheduleSlot {
        Objects.requireNonNull(day, "day");
        Objects.requireNonNull(period, "period");
    }

    public HoldingRegisterPoint speedTemperatureRegister() {
        final int offset = (period.value() - 1) * 2;
        return new HoldingRegisterPoint(new StartAddress(day.firstScheduleRegister() + offset));
    }
}