package io.oodesigns.modbus.value;

import io.oodesigns.modbus.core.Precondition;
import java.time.Duration;

/**
 * How often a device is polled while it is running.
 *
 * @param value the interval, validated on construction
 */
public record PollInterval(Duration value) {

    /**
     * @param value a strictly positive duration
     * @throws IllegalArgumentException if the interval is missing, zero or negative
     */
    public PollInterval {
        Precondition.required(value, "poll interval");
        Precondition.require(!value.isZero() && !value.isNegative(),
                "poll interval must be positive but was " + value);
    }
}
