package com.oodesigns.devicecomms.domain.connection;

import com.oodesigns.devicecomms.domain.value.IPAddress;
import com.oodesigns.devicecomms.domain.value.Port;
import com.oodesigns.devicecomms.domain.value.Timeout;
import java.util.Objects;

public record NetworkEndpoint(IPAddress host, Port port, Timeout timeout) implements ConnectionSettings {
    public NetworkEndpoint {
        Objects.requireNonNull(host, "host");
        Objects.requireNonNull(port, "port");
        Objects.requireNonNull(timeout, "timeout");
    }
}