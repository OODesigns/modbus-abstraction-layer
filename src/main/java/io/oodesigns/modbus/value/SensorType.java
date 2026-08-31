package io.oodesigns.modbus.value;
public record SensorType(String value) { public SensorType { if (value == null || !value.matches("[a-z][a-z0-9_-]*")) throw new IllegalArgumentException("invalid sensor type"); } }
