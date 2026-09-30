package com.oodesigns.devicecomms.device.waveshare.analoginput.value;

public final class AnalogInputRangeMode {
    public static final AnalogInputRangeMode VOLTAGE_ZERO_BASED = new AnalogInputRangeMode();
    public static final AnalogInputRangeMode VOLTAGE_OFFSET = new AnalogInputRangeMode();
    public static final AnalogInputRangeMode CURRENT_ZERO_BASED = new AnalogInputRangeMode();
    public static final AnalogInputRangeMode CURRENT_OFFSET = new AnalogInputRangeMode();
    public static final AnalogInputRangeMode ADC_SCALE_CODE = new AnalogInputRangeMode();

    private AnalogInputRangeMode() {
    }

}