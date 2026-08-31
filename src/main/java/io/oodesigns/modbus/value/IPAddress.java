package io.oodesigns.modbus.value;

import io.oodesigns.modbus.core.Precondition;
import java.util.regex.Pattern;

/**
 * An IPv4 address of a Modbus TCP device.
 *
 * @param value the dotted-quad address, validated on construction
 */
public record IPAddress(String value) {

    private static final Pattern DOTTED_QUAD =
            Pattern.compile("^((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$");

    /**
     * @param value dotted-quad IPv4 address such as {@code 192.168.1.10}
     * @throws IllegalArgumentException if the address is missing or not a valid dotted quad
     */
    public IPAddress {
        Precondition.requiredText(value, "IP address");
        Precondition.require(DOTTED_QUAD.matcher(value).matches(), "IP address is not a valid IPv4 address: " + value);
    }
}
