package com.oodesigns.devicecomms.device.blauberg.command;

import com.oodesigns.devicecomms.domain.device.DeviceCommand;

public record SetControllerPower(boolean enabled) implements DeviceCommand {
}