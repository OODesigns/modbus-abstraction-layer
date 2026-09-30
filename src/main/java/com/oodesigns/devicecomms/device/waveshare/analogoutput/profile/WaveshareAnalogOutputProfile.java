package com.oodesigns.devicecomms.device.waveshare.analogoutput.profile;

import com.oodesigns.devicecomms.adapter.modbus.ModbusTransportKeys;
import com.oodesigns.devicecomms.domain.connection.SerialEndpoint;
import com.oodesigns.devicecomms.domain.device.ConfigLoader;
import com.oodesigns.devicecomms.domain.value.BaudRate;
import com.oodesigns.devicecomms.domain.value.SerialPortName;
import com.oodesigns.devicecomms.domain.value.Timeout;
import com.oodesigns.devicecomms.domain.communication.TransportKey;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class WaveshareAnalogOutputProfile {
    private static final Set<Integer> SUPPORTED_BAUD_RATES = Set.of(4800, 9600, 19200, 38400,
            57600, 115200, 128000, 256000);

    private final SerialEndpoint endpoint;

    public WaveshareAnalogOutputProfile(final ConfigLoader config) {
        Objects.requireNonNull(config, "config");
        final Map<String, Object> values = config.values();
        final Object transport = values.get("transport");
        if (!(transport instanceof String transportName) || !transportName.equals("modbus-rtu")) {
            throw new IllegalArgumentException("Waveshare analog output requires modbus-rtu transport");
        }
        final Object serialPort = values.get("serialPort");
        if (!(serialPort instanceof String serialPortName) || serialPortName.isBlank()) {
            throw new IllegalArgumentException("serialPort is required");
        }
        final Object baudRate = values.get("baudRate");
        if (!(baudRate instanceof Number baudRateNumber)
                || !Double.isFinite(baudRateNumber.doubleValue())
                || baudRateNumber.doubleValue() < Integer.MIN_VALUE
                || baudRateNumber.doubleValue() > Integer.MAX_VALUE
                || baudRateNumber.doubleValue() != Math.rint(baudRateNumber.doubleValue())) {
            throw new IllegalArgumentException("baudRate must be an integer");
        }
        if (!SUPPORTED_BAUD_RATES.contains(baudRateNumber.intValue())) {
            throw new IllegalArgumentException("baudRate is not supported by the Waveshare analog output module");
        }
        final Object timeout = values.get("timeoutMillis");
        if (!(timeout instanceof Number timeoutNumber)
                || !Double.isFinite(timeoutNumber.doubleValue())
                || timeoutNumber.doubleValue() < 1
                || timeoutNumber.doubleValue() >= Long.MAX_VALUE
                || timeoutNumber.doubleValue() != Math.rint(timeoutNumber.doubleValue())) {
            throw new IllegalArgumentException("timeoutMillis must be a positive integer");
        }
        endpoint = new SerialEndpoint(new SerialPortName(serialPortName), new BaudRate(baudRateNumber.intValue()),
                new Timeout(Duration.ofMillis(timeoutNumber.longValue())));
    }

    public TransportKey transport() {
        return ModbusTransportKeys.RTU;
    }

    public SerialEndpoint endpoint() {
        return endpoint;
    }
}