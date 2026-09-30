package com.oodesigns.devicecomms.device.blauberg.command;

import com.oodesigns.devicecomms.device.blauberg.value.ScheduleSlot;
import com.oodesigns.devicecomms.device.blauberg.value.WeeklyTemperatureSetpointCelsius;
import com.oodesigns.devicecomms.domain.device.DeviceCommand;
import java.util.Objects;

public record UpdateWeeklyTemperature(ScheduleSlot slot,
                                      WeeklyTemperatureSetpointCelsius temperature) implements DeviceCommand {
    public UpdateWeeklyTemperature {
        Objects.requireNonNull(slot, "slot");
        Objects.requireNonNull(temperature, "temperature");
    }
}