package com.oodesigns.devicecomms.device.sensors.profile;

import com.oodesigns.devicecomms.device.waveshare.analoginput.reading.AnalogChannelReading;
import com.oodesigns.devicecomms.device.waveshare.analoginput.value.AnalogInputRangeMode;
import com.oodesigns.devicecomms.device.waveshare.analoginput.value.CurrentMicroamps;
import com.oodesigns.devicecomms.device.waveshare.analoginput.value.VoltageMillivolts;
import com.oodesigns.devicecomms.domain.Response;

public final class ElectricalSignalMode {
    public static final ElectricalSignalMode VOLTAGE_0_TO_10 =
            new ElectricalSignalMode(AnalogInputRangeMode.VOLTAGE_ZERO_BASED, 0, 10000);
    public static final ElectricalSignalMode CURRENT_4_TO_20 =
            new ElectricalSignalMode(AnalogInputRangeMode.CURRENT_OFFSET, 4000, 20000);

    private final AnalogInputRangeMode channelRangeMode;
    private final RawSignalRange rawRange;

    private ElectricalSignalMode(final AnalogInputRangeMode channelRangeMode,
                                 final int rawMinimum, final int rawMaximum) {
        this.channelRangeMode = channelRangeMode;
        rawRange = new RawSignalRange(rawMinimum, rawMaximum);
    }

    Response<RawSignalValue> read(final AnalogChannelReading reading) {
        if (reading.rangeMode() != channelRangeMode) {
            return Response.failure("analog channel is configured for a different electrical range");
        }
        final int rawValue;
        if (reading.measurement() instanceof VoltageMillivolts voltage) {
            rawValue = voltage.value();
        } else if (reading.measurement() instanceof CurrentMicroamps current) {
            rawValue = current.value();
        } else {
            return Response.failure("analog measurement type does not match the configured electrical mode");
        }
        if (rawValue < rawRange.minimum() || rawValue > rawRange.maximum()) {
            return Response.failure("analog reading is outside the configured electrical range");
        }
        return Response.success(new RawSignalValue(rawValue));
    }

    RawSignalRange rawRange() {
        return rawRange;
    }
}