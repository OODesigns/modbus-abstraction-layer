package io.oodesigns.modbus.value;
public record DeviceType(String value) { public DeviceType { if (value == null || !value.matches("[a-z][a-z0-9_-]*")) throw new IllegalArgumentException("invalid device type"); } }
