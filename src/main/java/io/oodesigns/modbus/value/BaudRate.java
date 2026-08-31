package io.oodesigns.modbus.value;

import io.oodesigns.modbus.core.Precondition;
import java.util.Set;

/**
 * A standard serial line speed.
 *
 * @param value the baud rate, validated on construction
 */
public record BaudRate(int value) {

    private static final Set<Integer> STANDARD_RATES =
            Set.of(1200, 2400, 4800, 9600, 19200, 38400, 57600, 115200);

    /**
     * @param value one of the standard baud rates supported by Modbus RTU hardware
     * @throws IllegalArgumentException if the rate is not a standard baud rate
     */
    public BaudRate {
        Precondition.require(STANDARD_RATES.contains(value), "baud rate is not a standard rate: " + value);
    }
}
