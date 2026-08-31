package io.oodesigns.modbus.value;

import io.oodesigns.modbus.core.Precondition;

/**
 * The content of a single Modbus register, an unsigned 16-bit word.
 *
 * @param value the register content, validated on construction
 */
public record RegisterValue(int value) {

    private static final int LOWEST = 0;
    private static final int HIGHEST = 65535;
    private static final int SIGN_BIT = 32768;
    private static final int WORD = 65536;

    /**
     * @param value register content, between 0 and 65535
     * @throws IllegalArgumentException if the content does not fit an unsigned 16-bit word
     */
    public RegisterValue {
        Precondition.inRange(value, LOWEST, HIGHEST, "register value");
    }

    /** @return the register read as a signed 16-bit word, as most sensors encode it */
    public int asSigned() {
        return value >= SIGN_BIT ? value - WORD : value;
    }
}
