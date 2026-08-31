package io.oodesigns.modbus.value;

import io.oodesigns.modbus.core.Precondition;

/**
 * A TCP port number.
 *
 * @param value the port, validated on construction
 */
public record Port(int value) {

    private static final int LOWEST = 1;
    private static final int HIGHEST = 65535;

    /** Modbus TCP's registered port. */
    public static Port modbusDefault() {
        return new Port(502);
    }

    /**
     * @param value port number, between 1 and 65535
     * @throws IllegalArgumentException if the port lies outside the valid range
     */
    public Port {
        Precondition.inRange(value, LOWEST, HIGHEST, "port");
    }
}
