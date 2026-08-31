package io.oodesigns.modbus.value;

import io.oodesigns.modbus.core.Precondition;

/**
 * A temperature reading in degrees Celsius, validated on construction against the range the
 * sensor that produced it is able to report.
 */
public final class TemperatureCelsius {

    /** Absolute zero, the physical limit no temperature range may cross. */
    public static final double ABSOLUTE_ZERO = -273.15;

    private static final LowTemperatureRange DEFAULT_LOW = new LowTemperatureRange(-50.0);
    private static final HighTemperatureRange DEFAULT_HIGH = new HighTemperatureRange(150.0);

    private final double value;

    /**
     * @param value temperature within the default sensor range of -50 to 150 degrees Celsius
     * @throws IllegalArgumentException if the temperature is not a number or lies outside the
     *     default range
     */
    public TemperatureCelsius(double value) {
        this(value, DEFAULT_LOW, DEFAULT_HIGH);
    }

    /**
     * @param value temperature to record
     * @param low lowest temperature the sensor may report
     * @param high highest temperature the sensor may report
     * @throws IllegalArgumentException if a boundary is missing, the range is not ordered low
     *     before high, or the temperature is not a number inside that range
     */
    public TemperatureCelsius(double value, LowTemperatureRange low, HighTemperatureRange high) {
        Precondition.required(low, "low temperature range");
        Precondition.required(high, "high temperature range");
        Precondition.require(low.value() <= high.value(),
                "temperature range must be ordered low before high but was " + low.value() + ".." + high.value());
        this.value = Precondition.inRange(value, low.value(), high.value(), "temperature");
    }

    /** @return the temperature in degrees Celsius */
    public double value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof TemperatureCelsius temperature
                && Double.compare(value, temperature.value) == 0;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(value);
    }

    @Override
    public String toString() {
        return value + "C";
    }
}
