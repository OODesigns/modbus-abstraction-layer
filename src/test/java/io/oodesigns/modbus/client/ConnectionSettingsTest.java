package io.oodesigns.modbus.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.oodesigns.modbus.value.BaudRate;
import io.oodesigns.modbus.value.IPAddress;
import io.oodesigns.modbus.value.Port;
import io.oodesigns.modbus.value.Retries;
import io.oodesigns.modbus.value.SerialPortName;
import io.oodesigns.modbus.value.Timeout;
import io.oodesigns.modbus.value.UnitId;
import java.time.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ConnectionSettings")
class ConnectionSettingsTest {

    private static final UnitId UNIT = new UnitId(3);
    private static final Timeout TIMEOUT = new Timeout(Duration.ofSeconds(2));
    private static final Retries RETRIES = new Retries(2);

    @Test
    @DisplayName("TCP settings expose host and port and no serial details")
    void tcpSettings() {
        ConnectionSettings settings = ConnectionSettings.forTcp(
                new IPAddress("10.0.0.2"), new Port(502), UNIT, TIMEOUT, RETRIES);

        assertEquals(TransportType.TCP, settings.transport());
        assertEquals("10.0.0.2", settings.host().orElseThrow().value());
        assertEquals(502, settings.port().orElseThrow().value());
        assertTrue(settings.serialPort().isEmpty());
        assertTrue(settings.baudRate().isEmpty());
        assertEquals(UNIT, settings.unitId());
        assertEquals(TIMEOUT, settings.timeout());
        assertEquals(RETRIES, settings.retries());
    }

    @Test
    @DisplayName("RTU settings expose serial port and baud rate and no network details")
    void rtuSettings() {
        ConnectionSettings settings = ConnectionSettings.forRtu(
                new SerialPortName("/dev/ttyUSB0"), new BaudRate(19200), UNIT, TIMEOUT, RETRIES);

        assertEquals(TransportType.RTU, settings.transport());
        assertEquals("/dev/ttyUSB0", settings.serialPort().orElseThrow().value());
        assertEquals(19200, settings.baudRate().orElseThrow().value());
        assertTrue(settings.host().isEmpty());
        assertTrue(settings.port().isEmpty());
    }

    @Test
    @DisplayName("every collaborating value object is a construction precondition")
    void collaboratorsAreRequired() {
        assertThrows(IllegalArgumentException.class,
                () -> ConnectionSettings.forTcp(null, new Port(502), UNIT, TIMEOUT, RETRIES));
        assertThrows(IllegalArgumentException.class,
                () -> ConnectionSettings.forTcp(new IPAddress("10.0.0.2"), null, UNIT, TIMEOUT, RETRIES));
        assertThrows(IllegalArgumentException.class,
                () -> ConnectionSettings.forTcp(new IPAddress("10.0.0.2"), new Port(502), null, TIMEOUT, RETRIES));
        assertThrows(IllegalArgumentException.class,
                () -> ConnectionSettings.forRtu(null, new BaudRate(9600), UNIT, TIMEOUT, RETRIES));
        assertThrows(IllegalArgumentException.class,
                () -> ConnectionSettings.forRtu(new SerialPortName("COM1"), null, UNIT, TIMEOUT, RETRIES));
    }
}
