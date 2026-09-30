package com.oodesigns.devicecomms.device.sensors.profile;

public enum ParticulateRange {
    MICROGRAMS_0_TO_50(50),
    MICROGRAMS_0_TO_100(100),
    MICROGRAMS_0_TO_300(300),
    MICROGRAMS_0_TO_500(500);

    private final int maximumMicrogramsPerCubicMetre;

    ParticulateRange(final int maximumMicrogramsPerCubicMetre) {
        this.maximumMicrogramsPerCubicMetre = maximumMicrogramsPerCubicMetre;
    }

    public int maximumMicrogramsPerCubicMetre() {
        return maximumMicrogramsPerCubicMetre;
    }
}