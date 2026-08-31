package io.oodesigns.modbus.value;
public record DependencyKey(String value) { public DependencyKey { if (value == null || !value.matches("[a-z][a-z0-9_-]*")) throw new IllegalArgumentException("invalid dependency key"); } }
