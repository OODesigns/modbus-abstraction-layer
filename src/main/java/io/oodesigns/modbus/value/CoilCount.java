package io.oodesigns.modbus.value;
public record CoilCount(int value) { public CoilCount { if (value < 1 || value > 2000) throw new IllegalArgumentException("coil count must be 1..2000"); } }
