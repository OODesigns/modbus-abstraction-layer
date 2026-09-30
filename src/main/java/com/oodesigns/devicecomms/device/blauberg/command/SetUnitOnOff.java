package com.oodesigns.devicecomms.device.blauberg.command;

import com.oodesigns.devicecomms.domain.device.DeviceCommand;

public record SetUnitOnOff(boolean enabled) implements DeviceCommand {
}