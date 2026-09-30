package com.oodesigns.devicecomms.device.blauberg.value;

public record AirflowRate(int value) {
    public AirflowRate {
        if (value < 0 || value > 10000) {
            throw new IllegalArgumentException("airflow must be between 0 and 10000 cubic metres per hour");
        }
    }
}