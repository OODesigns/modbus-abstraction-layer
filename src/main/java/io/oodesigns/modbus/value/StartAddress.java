package io.oodesigns.modbus.value;
public record StartAddress(int value) { public StartAddress { if (value < 0 || value > 65535) throw new IllegalArgumentException("start address must be 0..65535"); } }
