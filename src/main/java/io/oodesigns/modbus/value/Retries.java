package io.oodesigns.modbus.value;

import io.oodesigns.modbus.core.Precondition;

/**
 * How many additional attempts an operation is granted after its first failure.
 *
 * @param value the retry count, validated on construction
 */
public record Retries(int value) {

    private static final int NONE = 0;
    private static final int MOST = 10;

    /**
     * @param value retry count, between 0 and 10
     * @throws IllegalArgumentException if the count lies outside the supported range
     */
    public Retries {
        Precondition.inRange(value, NONE, MOST, "retries");
    }

    /** @return the total number of attempts, that is the first attempt plus the retries */
    public int attempts() {
        return value + 1;
    }
}
