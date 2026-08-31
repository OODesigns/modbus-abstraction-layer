package io.oodesigns.modbus.value;

import io.oodesigns.modbus.core.Precondition;

/**
 * How many registers a single Modbus read request asks for.
 *
 * @param value the count, validated on construction
 */
public record RegisterCount(int value) {

    private static final int FEWEST = 1;
    private static final int MOST = 125;

    /**
     * @param value register count, between 1 and 125 as allowed by the Modbus specification
     * @throws IllegalArgumentException if the count lies outside the specified range
     */
    public RegisterCount {
        Precondition.inRange(value, FEWEST, MOST, "register count");
    }
}
