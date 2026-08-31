package io.oodesigns.modbus.value;
public record UnitId(int value) { public UnitId { if (value < 0 || value > 247) throw new IllegalArgumentException("unit id must be 0..247"); } }
