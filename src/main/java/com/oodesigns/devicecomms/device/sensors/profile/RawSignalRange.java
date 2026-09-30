package com.oodesigns.devicecomms.device.sensors.profile;

public record RawSignalRange(int minimum, int maximum) {
    public RawSignalRange {
        if (minimum < 0 || maximum <= minimum || maximum > 20000) {
            throw new IllegalArgumentException("raw signal range is invalid");
        }
    }
}