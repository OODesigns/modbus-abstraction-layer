package io.oodesigns.modbus.value;

import io.oodesigns.modbus.core.Precondition;
import java.util.regex.Pattern;

/**
 * Identifier for the device type published by a plugin, such as {@code blauberg_mvhr}.
 *
 * @param value the identifier, validated on construction
 */
public record DeviceType(String value) {

    private static final Pattern IDENTIFIER = Pattern.compile("^[A-Za-z0-9._-]+$");

    /**
     * @param value a non-blank identifier made of letters, digits, dots, dashes or underscores
     * @throws IllegalArgumentException if the identifier is missing, blank or malformed
     */
    public DeviceType {
        Precondition.requiredText(value, "DeviceType");
        Precondition.require(IDENTIFIER.matcher(value).matches(), "DeviceType is not a valid identifier: " + value);
    }
}
