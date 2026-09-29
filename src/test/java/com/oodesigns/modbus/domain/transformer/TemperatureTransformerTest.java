package com.oodesigns.modbus.domain.transformer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.oodesigns.modbus.domain.Response;
import org.junit.jupiter.api.Test;

class TemperatureTransformerTest {
    private final TemperatureTransformer transformer = new TemperatureTransformer();

    @Test
    void transformsValidNumericValues() {
        final var result = transformer.transform(Response.success(21.5));

        assertEquals(Response.Status.OK, result.status());
        assertEquals(21.5, result.value().value());
    }

    @Test
    void preservesAnIncomingFailureAndConvertsInvalidValuesToFailure() {
        final Response<Number> failure = Response.failure("sensor unavailable");

        assertSame(failure, (Object) transformer.transform(failure));
        assertEquals(Response.Status.EXCEPTION,
                transformer.transform(Response.success(Double.NaN)).status());
    }
}