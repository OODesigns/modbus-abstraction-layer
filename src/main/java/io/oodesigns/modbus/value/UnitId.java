package io.oodesigns.modbus.value;

import io.oodesigns.modbus.core.Precondition;

/**
 * The Modbus slave (unit) identifier addressed on the bus.
 *
 * @param value the unit id, validated on construction
 */
public record UnitId(int value) {

    private static final int LOWEST = 1;
    private static final int HIGHEST = 247;

    /**
     * @param value unit id, between 1 and 247 as defined by the Modbus specification
     * @throws IllegalArgumentException if the unit id lies outside the addressable range
     */
    public UnitId {
        Precondition.inRange(value, LOWEST, HIGHEST, "unit id");
    }
}
