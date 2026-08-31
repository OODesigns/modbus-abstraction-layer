package io.oodesigns.modbus.value;
public record SerialPortName(String value) { public SerialPortName { if (value == null || value.isBlank()) throw new IllegalArgumentException("serial port name is required"); } }
