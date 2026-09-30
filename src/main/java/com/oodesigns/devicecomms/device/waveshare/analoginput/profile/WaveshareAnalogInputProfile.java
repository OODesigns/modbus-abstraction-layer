package com.oodesigns.devicecomms.device.waveshare.analoginput.profile;

import com.oodesigns.devicecomms.adapter.modbus.ModbusTransportKeys;
import com.oodesigns.devicecomms.device.waveshare.analoginput.value.ModuleRevision;
import com.oodesigns.devicecomms.domain.connection.SerialEndpoint;
import com.oodesigns.devicecomms.domain.device.ConfigLoader;
import com.oodesigns.devicecomms.domain.value.BaudRate;
import com.oodesigns.devicecomms.domain.value.Timeout;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;

public final class WaveshareAnalogInputProfile {
    private final SerialEndpoint endpoint;
    private final AnalogInputProfile analogInputProfile;

    public WaveshareAnalogInputProfile(final ConfigLoader config) {
        Objects.requireNonNull(config, "config");
        final Map<String, Object> values = config.values();
        final Object rawTransport = values.get("transport");
        if (!(rawTransport instanceof String transport) || !transport.equals("modbus-rtu")) {
            throw new IllegalArgumentException("Waveshare Analog Input 8CH requires modbus-rtu transport");
        }
        final Object rawSerialPort = values.get("serialPort");
        if (!(rawSerialPort instanceof String serialPort) || serialPort.isBlank()) {
            throw new IllegalArgumentException("serialPort is required");
        }
        final Object rawBaudRate = values.get("baudRate");
        if (!(rawBaudRate instanceof Number baudRateNumber)
                || !Double.isFinite(baudRateNumber.doubleValue())
                || baudRateNumber.doubleValue() < Integer.MIN_VALUE
                || baudRateNumber.doubleValue() > Integer.MAX_VALUE
                || baudRateNumber.doubleValue() != Math.rint(baudRateNumber.doubleValue())) {
            throw new IllegalArgumentException("baudRate must be an integer");
        }
        final Object rawTimeout = values.get("timeoutMillis");
        if (!(rawTimeout instanceof Number timeoutNumber)
                || !Double.isFinite(timeoutNumber.doubleValue())
                || timeoutNumber.doubleValue() < 1
                || timeoutNumber.doubleValue() >= Long.MAX_VALUE
                || timeoutNumber.doubleValue() != Math.rint(timeoutNumber.doubleValue())) {
            throw new IllegalArgumentException("timeoutMillis must be a positive integer");
        }
        final Object rawRevision = values.get("moduleRevision");
        if (!(rawRevision instanceof String revisionName)) {
            throw new IllegalArgumentException("moduleRevision must be A or B");
        }
        final ModuleRevision revision = switch (revisionName) {
            case "A" -> ModuleRevision.A;
            case "B" -> ModuleRevision.B;
            default -> throw new IllegalArgumentException("moduleRevision must be A or B");
        };
        endpoint = new SerialEndpoint(new com.oodesigns.devicecomms.domain.value.SerialPortName(serialPort),
                new BaudRate(baudRateNumber.intValue()),
                new Timeout(Duration.ofMillis(timeoutNumber.longValue())));
        analogInputProfile = new AnalogInputProfile(revision);
    }

    public com.oodesigns.devicecomms.domain.communication.TransportKey transport() {
        return ModbusTransportKeys.RTU;
    }

    public SerialEndpoint endpoint() {
        return endpoint;
    }

    public AnalogInputProfile analogInputProfile() {
        return analogInputProfile;
    }
}