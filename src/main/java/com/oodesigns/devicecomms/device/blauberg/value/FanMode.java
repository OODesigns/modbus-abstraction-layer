package com.oodesigns.devicecomms.device.blauberg.value;

public final class FanMode {
    public static final FanMode SPEED_1 = new FanMode(1);
    public static final FanMode SPEED_2 = new FanMode(2);
    public static final FanMode SPEED_3 = new FanMode(3);
    public static final FanMode SPEED_4 = new FanMode(4);
    public static final FanMode SPEED_5 = new FanMode(5);
    public static final FanMode MANUAL = new FanMode(255);

    private final int registerValue;

    private FanMode(final int registerValue) {
        this.registerValue = registerValue;
    }

    public int registerValue() {
        return registerValue;
    }
}