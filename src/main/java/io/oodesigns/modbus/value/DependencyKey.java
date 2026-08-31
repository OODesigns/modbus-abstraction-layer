package io.oodesigns.modbus.value;

import io.oodesigns.modbus.core.Precondition;
import java.util.regex.Pattern;

/**
 * Identifier for the name under which a collaborator is published to plugins.
 *
 * @param value the identifier, validated on construction
 */
public record DependencyKey(String value) {

    private static final Pattern IDENTIFIER = Pattern.compile("^[A-Za-z0-9._-]+$");

    /**
     * @param value a non-blank identifier made of letters, digits, dots, dashes or underscores
     * @throws IllegalArgumentException if the identifier is missing, blank or malformed
     */
    public DependencyKey {
        Precondition.requiredText(value, "DependencyKey");
        Precondition.require(IDENTIFIER.matcher(value).matches(), "DependencyKey is not a valid identifier: " + value);
    }
}
