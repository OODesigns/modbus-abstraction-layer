package io.oodesigns.modbus.value;

import io.oodesigns.modbus.core.Precondition;
import java.util.regex.Pattern;

/**
 * Identifier for the kind of sensor a response transformer serves, such as {@code temperature}.
 *
 * @param value the identifier, validated on construction
 */
public record SensorType(String value) {

    private static final Pattern IDENTIFIER = Pattern.compile("^[A-Za-z0-9._-]+$");

    /**
     * @param value a non-blank identifier made of letters, digits, dots, dashes or underscores
     * @throws IllegalArgumentException if the identifier is missing, blank or malformed
     */
    public SensorType {
        Precondition.requiredText(value, "SensorType");
        Precondition.require(IDENTIFIER.matcher(value).matches(), "SensorType is not a valid identifier: " + value);
    }
}
