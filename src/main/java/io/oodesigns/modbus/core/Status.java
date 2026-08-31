package io.oodesigns.modbus.core;

/**
 * Outcome of an operation.
 *
 * <p>Mirrors the two states a {@link Response} can be in: a successful result or a recorded
 * failure. There is deliberately no third state — absence of a value is still a status.</p>
 */
public enum Status {

    /** The operation completed as intended. */
    OK,

    /** The operation did not complete; {@link Response#details()} explains why. */
    EXCEPTION
}
