package io.oodesigns.modbus.value;

import io.oodesigns.modbus.core.Precondition;
import java.util.regex.Pattern;

/**
 * The operating-system name of a serial port used for Modbus RTU.
 *
 * @param value the port name, validated on construction
 */
public record SerialPortName(String value) {

    private static final Pattern PORT_NAME = Pattern.compile("^(/dev/[A-Za-z0-9._-]+|COM[1-9][0-9]*)$");

    /**
     * @param value a device path such as {@code /dev/ttyUSB0} or a Windows name such as {@code COM1}
     * @throws IllegalArgumentException if the name is missing or not a usable serial port name
     */
    public SerialPortName {
        Precondition.requiredText(value, "serial port name");
        Precondition.require(PORT_NAME.matcher(value).matches(), "serial port name is not usable: " + value);
    }
}
