package com.oodesigns.modbus.domain.connection;

import com.oodesigns.modbus.domain.value.IPAddress;
import com.oodesigns.modbus.domain.value.Port;
import com.oodesigns.modbus.domain.value.Timeout;
import java.util.Objects;

public record NetworkEndpoint(IPAddress host, Port port, Timeout timeout) implements ConnectionSettings {
    public NetworkEndpoint {
        Objects.requireNonNull(host, "host");
        Objects.requireNonNull(port, "port");
        Objects.requireNonNull(timeout, "timeout");
    }
}