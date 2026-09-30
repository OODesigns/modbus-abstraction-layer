package com.oodesigns.devicecomms.device.sensors.profile;

public enum CarbonDioxideRange {
    PPM_0_TO_2000(2000),
    PPM_0_TO_5000(5000);

    private final int maximumPpm;

    CarbonDioxideRange(final int maximumPpm) {
        this.maximumPpm = maximumPpm;
    }

    public int maximumPpm() {
        return maximumPpm;
    }
}