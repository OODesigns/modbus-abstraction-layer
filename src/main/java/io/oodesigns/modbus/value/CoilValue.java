package io.oodesigns.modbus.value;

/**
 * The state of a single Modbus coil or discrete input.
 *
 * @param value {@code true} when the coil is on
 */
public record CoilValue(boolean value) {
}
