package io.oodesigns.modbus.value;
public record Port(int value) { public Port { if (value < 1 || value > 65535) throw new IllegalArgumentException("port must be 1..65535"); } }
