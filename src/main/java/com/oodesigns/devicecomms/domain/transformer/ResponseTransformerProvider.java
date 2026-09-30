package com.oodesigns.devicecomms.domain.transformer;

import com.oodesigns.devicecomms.domain.value.SensorType;

public interface ResponseTransformerProvider {
    SensorType sensorType();

    ResponseTransformer<?, ?> transformer();
}