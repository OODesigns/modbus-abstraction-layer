package io.oodesigns.modbus.value;

import io.oodesigns.modbus.core.Precondition;
import java.time.Duration;

/**
 * How long an operation may take before it is abandoned.
 *
 * @param value the duration, validated on construction
 */
public record Timeout(Duration value) {

    /**
     * @param value a strictly positive duration
     * @throws IllegalArgumentException if the duration is missing, zero or negative
     */
    public Timeout {
        Precondition.required(value, "timeout");
        Precondition.require(!value.isZero() && !value.isNegative(), "timeout must be positive but was " + value);
    }
}
