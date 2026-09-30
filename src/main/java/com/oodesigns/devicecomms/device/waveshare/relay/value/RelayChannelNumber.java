package com.oodesigns.devicecomms.device.waveshare.relay.value;

public record RelayChannelNumber(int value) {
    public RelayChannelNumber {
        if (value < 1 || value > 30) {
            throw new IllegalArgumentException("relay channel must be from 1 to 30");
        }
    }
}