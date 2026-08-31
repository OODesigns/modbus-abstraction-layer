package io.oodesigns.modbus.value;

import io.oodesigns.modbus.core.Precondition;

/**
 * The address of a single Modbus coil.
 *
 * @param value the address, validated on construction
 */
public record CoilAddress(int value) {

    private static final int LOWEST = 0;
    private static final int HIGHEST = 65535;

    /**
     * @param value address inside the Modbus address space, between 0 and 65535
     * @throws IllegalArgumentException if the address lies outside the Modbus address space
     */
    public CoilAddress {
        Precondition.inRange(value, LOWEST, HIGHEST, "address");
    }
}
