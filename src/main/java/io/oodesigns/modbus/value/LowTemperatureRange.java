package io.oodesigns.modbus.value;

import io.oodesigns.modbus.core.Precondition;

/**
 * The lowest temperature a sensor is allowed to report.
 *
 * @param value the boundary in degrees Celsius, validated on construction
 */
public record LowTemperatureRange(double value) {

    /**
     * @param value a real temperature at or above absolute zero
     * @throws IllegalArgumentException if the boundary is below absolute zero or not a number
     */
    public LowTemperatureRange {
        Precondition.inRange(value, TemperatureCelsius.ABSOLUTE_ZERO, Double.MAX_VALUE, "low temperature range");
    }
}
