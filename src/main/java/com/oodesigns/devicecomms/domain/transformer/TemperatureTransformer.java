package com.oodesigns.devicecomms.domain.transformer;

import com.oodesigns.devicecomms.domain.Response;
import com.oodesigns.devicecomms.domain.value.TemperatureCelsius;

public final class TemperatureTransformer implements ResponseTransformer<Number, TemperatureCelsius> {
    @Override
    public Response<TemperatureCelsius> transform(final Response<Number> raw) {
        if (raw == null) {
            return Response.failure("raw temperature response is required");
        }
        return raw.flatMap(value -> {
            if (value == null) {
                return Response.failure("raw temperature value is required");
            }
            try {
                return Response.success(new TemperatureCelsius(value.doubleValue()));
            } catch (final IllegalArgumentException exception) {
                return Response.failure("temperature is outside the supported range");
            }
        });
    }
}