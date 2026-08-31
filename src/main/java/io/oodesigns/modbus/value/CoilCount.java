package io.oodesigns.modbus.value;

import io.oodesigns.modbus.core.Precondition;

/**
 * How many coils a single Modbus read request asks for.
 *
 * @param value the count, validated on construction
 */
public record CoilCount(int value) {

    private static final int FEWEST = 1;
    private static final int MOST = 2000;

    /**
     * @param value coil count, between 1 and 2000 as allowed by the Modbus specification
     * @throws IllegalArgumentException if the count lies outside the specified range
     */
    public CoilCount {
        Precondition.inRange(value, FEWEST, MOST, "coil count");
    }
}
