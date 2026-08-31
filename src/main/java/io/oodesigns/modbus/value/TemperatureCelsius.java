package io.oodesigns.modbus.value;
/** Celsius measurement constrained to the supported sensor range [-100, 200]. */
public record TemperatureCelsius(double value) { public TemperatureCelsius { if (!Double.isFinite(value) || value < -100 || value > 200) throw new IllegalArgumentException("temperature must be -100..200 Celsius"); } }
