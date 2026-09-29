package com.oodesigns.modbus.domain.transformer;

import com.oodesigns.modbus.domain.value.SensorType;

public final class TemperatureTransformerProvider implements ResponseTransformerProvider {
    public static final SensorType SENSOR_TYPE = new SensorType("temperature");

    @Override
    public SensorType sensorType() {
        return SENSOR_TYPE;
    }

    @Override
    public ResponseTransformer<?, ?> transformer() {
        return new TemperatureTransformer();
    }
}