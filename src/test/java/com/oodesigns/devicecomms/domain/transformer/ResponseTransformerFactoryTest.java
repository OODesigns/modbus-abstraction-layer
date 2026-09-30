package com.oodesigns.devicecomms.domain.transformer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.oodesigns.devicecomms.domain.Response;
import com.oodesigns.devicecomms.domain.value.SensorType;
import java.util.List;
import org.junit.jupiter.api.Test;

class ResponseTransformerFactoryTest {
    @Test
    void discoversBuiltInTemperatureTransformerWithServiceLoader() {
        final ResponseTransformerFactory factory = new ResponseTransformerFactory();

        assertEquals(Response.Status.OK, factory.forSensor(new SensorType("temperature")).status());
    }

    @Test
    void resolvesRegisteredTransformerOrReturnsFailure() {
        final SensorType type = new SensorType("temperature");
        final TemperatureTransformer transformer = new TemperatureTransformer();
        final ResponseTransformerProvider provider = new ResponseTransformerProvider() {
            @Override
            public SensorType sensorType() {
                return type;
            }

            @Override
            public ResponseTransformer<?, ?> transformer() {
                return transformer;
            }
        };
        final ResponseTransformerFactory factory = new ResponseTransformerFactory(List.of(provider));

        assertSame(transformer, factory.forSensor(type).value());
        assertEquals(Response.Status.EXCEPTION, factory.forSensor(new SensorType("unknown")).status());
    }
}