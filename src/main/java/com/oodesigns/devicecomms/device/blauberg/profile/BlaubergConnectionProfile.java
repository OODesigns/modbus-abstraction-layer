package com.oodesigns.devicecomms.device.blauberg.profile;

import com.oodesigns.devicecomms.adapter.modbus.ModbusTransportKeys;
import com.oodesigns.devicecomms.domain.connection.ConnectionSettings;
import com.oodesigns.devicecomms.domain.connection.NetworkEndpoint;
import com.oodesigns.devicecomms.domain.connection.SerialEndpoint;
import com.oodesigns.devicecomms.domain.device.ConfigLoader;
import com.oodesigns.devicecomms.domain.value.BaudRate;
import com.oodesigns.devicecomms.domain.value.IPAddress;
import com.oodesigns.devicecomms.domain.value.Port;
import com.oodesigns.devicecomms.domain.value.SerialPortName;
import com.oodesigns.devicecomms.domain.value.Timeout;
import com.oodesigns.devicecomms.domain.communication.TransportKey;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;

public final class BlaubergConnectionProfile {
    private final TransportKey transport;
    private final ConnectionSettings endpoint;

    public BlaubergConnectionProfile(final ConfigLoader config) {
        Objects.requireNonNull(config, "config");
        final Map<String, Object> values = config.values();
        final Object rawTransport = values.get("transport");
        if (!(rawTransport instanceof String transportName)) {
            throw new IllegalArgumentException("configuration value is required: transport");
        }
        final Object rawTimeout = values.get("timeoutMillis");
        if (!(rawTimeout instanceof Number timeoutNumber)
                || !Double.isFinite(timeoutNumber.doubleValue())
            || timeoutNumber.doubleValue() != Math.rint(timeoutNumber.doubleValue())
            || timeoutNumber.doubleValue() < 1
            || timeoutNumber.doubleValue() >= Long.MAX_VALUE) {
            throw new IllegalArgumentException("integer configuration value is required: timeoutMillis");
        }
        final Timeout timeout = new Timeout(Duration.ofMillis(timeoutNumber.longValue()));
        switch (transportName) {
            case "modbus-tcp" -> {
                final Object rawHost = values.get("host");
                final Object rawPort = values.get("port");
                if (!(rawHost instanceof String host) || !(rawPort instanceof Number portNumber)
                        || !Double.isFinite(portNumber.doubleValue())
                    || portNumber.doubleValue() < Integer.MIN_VALUE
                    || portNumber.doubleValue() > Integer.MAX_VALUE
                        || portNumber.doubleValue() != Math.rint(portNumber.doubleValue())) {
                    throw new IllegalArgumentException("valid host and integer port are required for Modbus TCP");
                }
                transport = ModbusTransportKeys.TCP;
                endpoint = new NetworkEndpoint(new IPAddress(host), new Port(portNumber.intValue()), timeout);
            }
            case "modbus-rtu" -> {
                final Object rawSerialPort = values.get("serialPort");
                final Object rawBaudRate = values.get("baudRate");
                if (!(rawSerialPort instanceof String serialPort) || !(rawBaudRate instanceof Number baudRateNumber)
                        || !Double.isFinite(baudRateNumber.doubleValue())
                    || baudRateNumber.doubleValue() < Integer.MIN_VALUE
                    || baudRateNumber.doubleValue() > Integer.MAX_VALUE
                        || baudRateNumber.doubleValue() != Math.rint(baudRateNumber.doubleValue())) {
                    throw new IllegalArgumentException("valid serial port and integer baud rate are required for Modbus RTU");
                }
                transport = ModbusTransportKeys.RTU;
                endpoint = new SerialEndpoint(new SerialPortName(serialPort),
                        new BaudRate(baudRateNumber.intValue()), timeout);
            }
            default -> throw new IllegalArgumentException("unsupported Blauberg MVHR transport: " + transportName);
        }
    }

    public TransportKey transport() {
        return transport;
    }

    public ConnectionSettings endpoint() {
        return endpoint;
    }
}
