package com.oodesigns.modbus.domain.connection;

import com.oodesigns.modbus.domain.value.BaudRate;
import com.oodesigns.modbus.domain.value.SerialPortName;
import com.oodesigns.modbus.domain.value.Timeout;
import java.util.Objects;

public record SerialEndpoint(SerialPortName port, BaudRate baudRate, Timeout timeout) implements ConnectionSettings {
    public SerialEndpoint {
        Objects.requireNonNull(port, "port");
        Objects.requireNonNull(baudRate, "baudRate");
        Objects.requireNonNull(timeout, "timeout");
    }
}