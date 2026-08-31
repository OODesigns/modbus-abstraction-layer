package io.oodesigns.modbus.value;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("TemperatureCelsius")
class TemperatureCelsiusTest {

    @Test
    @DisplayName("accepts temperatures inside the default sensor range")
    void acceptsTemperaturesInDefaultRange() {
        assertEquals(21.5, new TemperatureCelsius(21.5).value());
        assertEquals(-50.0, new TemperatureCelsius(-50.0).value());
        assertEquals(150.0, new TemperatureCelsius(150.0).value());
    }

    @Test
    @DisplayName("rejects temperatures outside the default sensor range")
    void rejectsTemperaturesOutsideDefaultRange() {
        assertThrows(IllegalArgumentException.class, () -> new TemperatureCelsius(-50.1));
        assertThrows(IllegalArgumentException.class, () -> new TemperatureCelsius(150.1));
        assertThrows(IllegalArgumentException.class, () -> new TemperatureCelsius(Double.NaN));
    }

    @Test
    @DisplayName("honours an explicit low/high range")
    void honoursExplicitRange() {
        LowTemperatureRange low = new LowTemperatureRange(0.0);
        HighTemperatureRange high = new HighTemperatureRange(40.0);

        assertEquals(0.0, new TemperatureCelsius(0.0, low, high).value());
        assertEquals(40.0, new TemperatureCelsius(40.0, low, high).value());
        assertThrows(IllegalArgumentException.class, () -> new TemperatureCelsius(-0.1, low, high));
        assertThrows(IllegalArgumentException.class, () -> new TemperatureCelsius(40.1, low, high));
    }

    @Test
    @DisplayName("a range boundary can never be below absolute zero")
    void rangeBoundsAreValidated() {
        assertThrows(IllegalArgumentException.class, () -> new LowTemperatureRange(-273.16));
        assertThrows(IllegalArgumentException.class, () -> new HighTemperatureRange(-273.16));
        assertThrows(IllegalArgumentException.class, () -> new LowTemperatureRange(Double.NaN));
    }

    @Test
    @DisplayName("a range must be ordered low before high")
    void rangeMustBeOrdered() {
        LowTemperatureRange low = new LowTemperatureRange(50.0);
        HighTemperatureRange high = new HighTemperatureRange(10.0);

        assertThrows(IllegalArgumentException.class, () -> new TemperatureCelsius(20.0, low, high));
    }

    @Test
    @DisplayName("a range is required, never null")
    void rangeIsRequired() {
        assertThrows(IllegalArgumentException.class,
                () -> new TemperatureCelsius(20.0, null, new HighTemperatureRange(40.0)));
        assertThrows(IllegalArgumentException.class,
                () -> new TemperatureCelsius(20.0, new LowTemperatureRange(0.0), null));
    }
}
