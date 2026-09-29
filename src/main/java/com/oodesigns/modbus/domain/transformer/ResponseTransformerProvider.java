package com.oodesigns.modbus.domain.transformer;

import com.oodesigns.modbus.domain.value.SensorType;

public interface ResponseTransformerProvider {
    SensorType sensorType();

    ResponseTransformer<?, ?> transformer();
}