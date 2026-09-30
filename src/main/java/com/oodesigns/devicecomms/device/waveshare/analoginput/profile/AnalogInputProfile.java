package com.oodesigns.devicecomms.device.waveshare.analoginput.profile;

import com.oodesigns.devicecomms.device.waveshare.analoginput.value.AnalogInputRangeMode;
import com.oodesigns.devicecomms.device.waveshare.analoginput.value.ModuleRevision;
import java.util.Objects;

public final class AnalogInputProfile {
    private final VoltageInputRange zeroBasedVoltageRange;
    private final VoltageInputRange offsetVoltageRange;

    public AnalogInputProfile(final ModuleRevision revision) {
        Objects.requireNonNull(revision, "revision");
        zeroBasedVoltageRange = switch (revision) {
            case A -> new VoltageInputRange(0, 5000);
            case B -> new VoltageInputRange(0, 10000);
        };
        offsetVoltageRange = switch (revision) {
            case A -> new VoltageInputRange(1000, 5000);
            case B -> new VoltageInputRange(2000, 10000);
        };
    }

    public VoltageInputRange voltageRange(final AnalogInputRangeMode mode) {
        Objects.requireNonNull(mode, "mode");
        if (mode == AnalogInputRangeMode.VOLTAGE_ZERO_BASED) {
            return zeroBasedVoltageRange;
        }
        if (mode == AnalogInputRangeMode.VOLTAGE_OFFSET) {
            return offsetVoltageRange;
        }
        throw new IllegalArgumentException("range mode is not a voltage mode");
    }
}