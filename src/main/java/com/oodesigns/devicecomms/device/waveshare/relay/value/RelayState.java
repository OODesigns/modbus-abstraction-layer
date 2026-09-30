package com.oodesigns.devicecomms.device.waveshare.relay.value;

public enum RelayState {
    OFF(false),
    ON(true);

    private final boolean energized;

    RelayState(final boolean energized) {
        this.energized = energized;
    }

    public boolean energized() {
        return energized;
    }

    public static RelayState fromCoilState(final boolean energized) {
        return energized ? ON : OFF;
    }
}