package io.oodesigns.modbus.value;

import io.oodesigns.modbus.core.Precondition;
import java.util.Optional;

/** An immutable block of registers as read from a device. */
public final class RegisterValues {

    private final int[] values;

    /**
     * @param values the registers as read from the wire; copied defensively
     * @throws IllegalArgumentException if the registers are missing or any of them does not fit
     *     an unsigned 16-bit word
     */
    public RegisterValues(int[] values) {
        Precondition.required(values, "registers");
        this.values = values.clone();
        for (int value : this.values) {
            Precondition.inRange(value, 0, 65535, "register value");
        }
    }

    /**
     * @param index zero-based position inside the block
     * @return the register at that position, or empty when the block is shorter
     */
    public Optional<RegisterValue> at(int index) {
        return index < 0 || index >= values.length
                ? Optional.empty()
                : Optional.of(new RegisterValue(values[index]));
    }

    /** @return how many registers were read */
    public int size() {
        return values.length;
    }

    /** @return a copy of the registers; mutating it cannot affect this value object */
    public int[] asArray() {
        return values.clone();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof RegisterValues registers
                && java.util.Arrays.equals(values, registers.values);
    }

    @Override
    public int hashCode() {
        return java.util.Arrays.hashCode(values);
    }

    @Override
    public String toString() {
        return "RegisterValues" + java.util.Arrays.toString(values);
    }
}
