package com.oodesigns.devicecomms.device.blauberg.value;

public enum ScheduleDay {
    MONDAY(126),
    TUESDAY(134),
    WEDNESDAY(142),
    THURSDAY(150),
    FRIDAY(158),
    SATURDAY(166),
    SUNDAY(174);

    private final int firstScheduleRegister;

    ScheduleDay(final int firstScheduleRegister) {
        this.firstScheduleRegister = firstScheduleRegister;
    }

    int firstScheduleRegister() {
        return firstScheduleRegister;
    }
}