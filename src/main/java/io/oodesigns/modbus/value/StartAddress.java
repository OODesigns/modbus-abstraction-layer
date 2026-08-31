package io.oodesigns.modbus.value;

import io.oodesigns.modbus.core.Precondition;

/**
 * The first address of a Modbus read request.
 *
 * @param value the address, validated on construction
 */
public record StartAddress(int value) {

    private static final int LOWEST = 0;
    private static final int HIGHEST = 65535;

    /**
     * @param value address inside the Modbus address space, between 0 and 65535
     * @throws IllegalArgumentException if the address lies outside the Modbus address space
     */
    public StartAddress {
        Precondition.inRange(value, LOWEST, HIGHEST, "address");
    }
}
