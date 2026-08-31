package io.oodesigns.modbus.core;

/**
 * Guard clauses used by constructors to enforce design-by-contract preconditions.
 *
 * <p>These helpers are the only place, besides constructors themselves, where an exception is
 * raised: they exist purely so that a constructor can state its contract in one line.</p>
 */
public final class Precondition {

    private Precondition() {
    }

    /**
     * @param value value under test
     * @param description what the value represents, used in the failure message
     * @return the value when it is present
     * @throws IllegalArgumentException if {@code value} is {@code null}
     */
    public static <T> T required(T value, String description) {
        if (value == null) {
            throw new IllegalArgumentException(description + " is required");
        }
        return value;
    }

    /**
     * @param value text under test
     * @param description what the text represents, used in the failure message
     * @return the text when it holds a meaningful value
     * @throws IllegalArgumentException if {@code value} is {@code null} or blank
     */
    public static String requiredText(String value, String description) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(description + " must not be blank");
        }
        return value;
    }

    /**
     * @param value number under test
     * @param min inclusive lower bound
     * @param max inclusive upper bound
     * @param description what the number represents, used in the failure message
     * @return the number when it lies inside the range
     * @throws IllegalArgumentException if {@code value} lies outside {@code [min, max]}
     */
    public static int inRange(int value, int min, int max, String description) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(
                    description + " must be between " + min + " and " + max + " but was " + value);
        }
        return value;
    }

    /**
     * @param value number under test
     * @param min inclusive lower bound
     * @param max inclusive upper bound
     * @param description what the number represents, used in the failure message
     * @return the number when it is a real number inside the range
     * @throws IllegalArgumentException if {@code value} is not a number or lies outside the range
     */
    public static double inRange(double value, double min, double max, String description) {
        if (Double.isNaN(value) || value < min || value > max) {
            throw new IllegalArgumentException(
                    description + " must be between " + min + " and " + max + " but was " + value);
        }
        return value;
    }

    /**
     * @param condition condition that must hold
     * @param message explanation used when the condition does not hold
     * @throws IllegalArgumentException if {@code condition} is {@code false}
     */
    public static void require(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }
}
