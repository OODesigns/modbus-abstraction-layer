package io.oodesigns.modbus.value;

import io.oodesigns.modbus.core.Precondition;
import java.util.Optional;

/** An immutable block of coils or discrete inputs as read from a device. */
public final class CoilValues {

    private final boolean[] values;

    /**
     * @param values the coils as read from the wire; copied defensively
     * @throws IllegalArgumentException if the coils are missing
     */
    public CoilValues(boolean[] values) {
        Precondition.required(values, "coils");
        this.values = values.clone();
    }

    /**
     * @param index zero-based position inside the block
     * @return the coil at that position, or empty when the block is shorter
     */
    public Optional<CoilValue> at(int index) {
        return index < 0 || index >= values.length
                ? Optional.empty()
                : Optional.of(new CoilValue(values[index]));
    }

    /** @return how many coils were read */
    public int size() {
        return values.length;
    }

    /** @return a copy of the coils; mutating it cannot affect this value object */
    public boolean[] asArray() {
        return values.clone();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof CoilValues coils && java.util.Arrays.equals(values, coils.values);
    }

    @Override
    public int hashCode() {
        return java.util.Arrays.hashCode(values);
    }

    @Override
    public String toString() {
        return "CoilValues" + java.util.Arrays.toString(values);
    }
}
