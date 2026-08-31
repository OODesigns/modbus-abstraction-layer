package io.oodesigns.modbus.value;
public record RegisterCount(int value) { public RegisterCount { if (value < 1 || value > 125) throw new IllegalArgumentException("register count must be 1..125"); } }
