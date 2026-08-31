package io.oodesigns.modbus.runtime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.oodesigns.modbus.core.Response;
import io.oodesigns.modbus.core.Status;
import io.oodesigns.modbus.value.RegisterValues;
import io.oodesigns.modbus.value.SensorType;
import io.oodesigns.modbus.value.TemperatureCelsius;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Response transformers")
class ResponseTransformerTest {

    private static final SensorType TEMPERATURE = new SensorType("temperature");

    @Test
    @DisplayName("the factory discovers transformers through the ServiceLoader")
    void discoversTransformers() {
        Response<ResponseTransformer<RegisterValues, ?>> response =
                new ResponseTransformerFactory().forSensor(TEMPERATURE);

        assertEquals(Status.OK, response.status(), response.details());
        assertTrue(response.value().orElseThrow() instanceof TemperatureTransformer);
    }

    @Test
    @DisplayName("the factory reports an unknown sensor type as a failure Response")
    void unknownSensorTypeProducesFailure() {
        Response<ResponseTransformer<RegisterValues, ?>> response =
                new ResponseTransformerFactory().forSensor(new SensorType("humidity"));

        assertEquals(Status.EXCEPTION, response.status());
        assertTrue(response.details().contains("humidity"));
    }

    @Test
    @DisplayName("the factory never throws for a missing sensor type")
    void nullSensorTypeProducesFailure() {
        assertEquals(Status.EXCEPTION, new ResponseTransformerFactory().forSensor(null).status());
    }

    @Test
    @DisplayName("the temperature transformer scales a raw register into degrees Celsius")
    void transformsRawRegister() {
        Response<TemperatureCelsius> response =
                new TemperatureTransformer().transform(Response.success(new RegisterValues(new int[] {215})));

        assertEquals(21.5, response.value().orElseThrow().value());
    }

    @Test
    @DisplayName("the temperature transformer reads negative temperatures as signed words")
    void transformsNegativeTemperature() {
        Response<TemperatureCelsius> response =
                new TemperatureTransformer().transform(Response.success(new RegisterValues(new int[] {65486})));

        assertEquals(-5.0, response.value().orElseThrow().value());
    }

    @Test
    @DisplayName("an upstream failure cascades through the transformer unchanged")
    void cascadesUpstreamFailure() {
        Response<RegisterValues> upstream = Response.failure("Modbus exception code 2");

        Response<TemperatureCelsius> response = new TemperatureTransformer().transform(upstream);

        assertEquals(Status.EXCEPTION, response.status());
        assertEquals("Modbus exception code 2", response.details());
    }

    @Test
    @DisplayName("a reading outside the sensor range becomes a failure Response, never an exception")
    void outOfRangeReadingProducesFailure() {
        Response<TemperatureCelsius> response =
                new TemperatureTransformer().transform(Response.success(new RegisterValues(new int[] {30000})));

        assertEquals(Status.EXCEPTION, response.status());
    }

    @Test
    @DisplayName("an empty read becomes a failure Response")
    void emptyReadProducesFailure() {
        Response<TemperatureCelsius> response =
                new TemperatureTransformer().transform(Response.success(new RegisterValues(new int[0])));

        assertEquals(Status.EXCEPTION, response.status());
    }

    @Test
    @DisplayName("a missing raw response becomes a failure Response")
    void missingRawResponseProducesFailure() {
        assertEquals(Status.EXCEPTION, new TemperatureTransformer().transform(null).status());
    }

    @Test
    @DisplayName("the transformer declares the sensor type it serves")
    void declaresSensorType() {
        assertEquals(TEMPERATURE, new TemperatureTransformer().sensorType());
    }

    @Test
    @DisplayName("its collaborators are construction preconditions")
    void collaboratorsAreRequired() {
        assertThrows(IllegalArgumentException.class, () -> new ResponseTransformerFactory(null));
    }
}
