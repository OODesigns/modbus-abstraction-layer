package com.oodesigns.devicecomms.device.blauberg.protocol;

public record S21RegisterValue(int value) {
    public S21RegisterValue {
        if (value < 0 || value > 65535) {
            throw new IllegalArgumentException("S21 register value must be an unsigned 16-bit value");
        }
    }
}