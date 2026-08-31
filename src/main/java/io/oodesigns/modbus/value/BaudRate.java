package io.oodesigns.modbus.value;
public record BaudRate(int value) { public BaudRate { if (value <= 0) throw new IllegalArgumentException("baud rate must be positive"); } }
