package com.oodesigns.devicecomms.device.waveshare.relay.profile;

import com.oodesigns.devicecomms.adapter.modbus.ModbusTransportKeys;
import com.oodesigns.devicecomms.domain.connection.NetworkEndpoint;
import com.oodesigns.devicecomms.domain.device.ConfigLoader;
import com.oodesigns.devicecomms.domain.value.IPAddress;
import com.oodesigns.devicecomms.domain.value.Port;
import com.oodesigns.devicecomms.domain.value.Timeout;
import com.oodesigns.devicecomms.domain.communication.TransportKey;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;

public final class WaveshareRelayProfile {
    private final NetworkEndpoint endpoint;

    public WaveshareRelayProfile(final ConfigLoader config) {
        Objects.requireNonNull(config, "config");
        final Map<String, Object> values = config.values();
        final Object transport = values.get("transport");
        if (!(transport instanceof String transportName) || !transportName.equals("modbus-tcp")) {
            throw new IllegalArgumentException("Waveshare relay profile requires modbus-tcp transport");
        }
        final Object host = values.get("host");
        final Object port = values.get("port");
        if (!(host instanceof String hostName) || !(port instanceof Number portNumber)
                || !Double.isFinite(portNumber.doubleValue())
                || portNumber.doubleValue() < Integer.MIN_VALUE
                || portNumber.doubleValue() > Integer.MAX_VALUE
                || portNumber.doubleValue() != Math.rint(portNumber.doubleValue())) {
            throw new IllegalArgumentException("valid host and integer port are required for Modbus TCP");
        }
        final Object timeout = values.get("timeoutMillis");
        if (!(timeout instanceof Number timeoutNumber)
                || !Double.isFinite(timeoutNumber.doubleValue())
                || timeoutNumber.doubleValue() < 1
                || timeoutNumber.doubleValue() >= Long.MAX_VALUE
                || timeoutNumber.doubleValue() != Math.rint(timeoutNumber.doubleValue())) {
            throw new IllegalArgumentException("timeoutMillis must be a positive integer");
        }
        endpoint = new NetworkEndpoint(new IPAddress(hostName), new Port(portNumber.intValue()),
                new Timeout(Duration.ofMillis(timeoutNumber.longValue())));
    }

    public TransportKey transport() {
        return ModbusTransportKeys.TCP;
    }

    public NetworkEndpoint endpoint() {
        return endpoint;
    }
}