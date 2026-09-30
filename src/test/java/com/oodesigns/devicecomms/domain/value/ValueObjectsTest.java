package com.oodesigns.devicecomms.domain.value;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import org.junit.jupiter.api.Test;

class ValueObjectsTest {
    @Test
    void acceptsValidTransportValues() {
        assertEquals("192.168.1.10", new IPAddress("192.168.1.10").value());
        assertEquals(65535, new Port(65535).value());
        assertEquals("/dev/ttyUSB0", new SerialPortName("/dev/ttyUSB0").value());
        assertEquals(9600, new BaudRate(9600).value());
        assertEquals(247, new UnitId(247).value());
        assertEquals(Duration.ofSeconds(2), new Timeout(Duration.ofSeconds(2)).value());
        assertEquals(0, new Retries(0).value());
    }

    @Test
    void acceptsModbusBoundaries() {
        assertEquals(0, new StartAddress(0).value());
        assertEquals(65535, new StartAddress(65535).value());
        assertEquals(1, new RegisterCount(1).value());
        assertEquals(125, new RegisterCount(125).value());
        assertEquals(1, new CoilCount(1).value());
        assertEquals(2000, new CoilCount(2000).value());
    }

    @Test
    void acceptsNonBlankDomainKeys() {
        assertEquals("mvhr", new DeviceType("mvhr").value());
        assertEquals("communication", new DependencyKey("communication").value());
        assertEquals("temperature", new SensorType("temperature").value());
    }

    @Test
    void validatesNumericRangesAndRequiredValues() {
        assertThrows(IllegalArgumentException.class, () -> new Port(0));
        assertThrows(IllegalArgumentException.class, () -> new BaudRate(0));
        assertThrows(IllegalArgumentException.class, () -> new UnitId(248));
        assertThrows(IllegalArgumentException.class, () -> new Timeout(Duration.ZERO));
        assertThrows(IllegalArgumentException.class, () -> new Retries(-1));
        assertThrows(IllegalArgumentException.class, () -> new StartAddress(65536));
        assertThrows(IllegalArgumentException.class, () -> new RegisterCount(126));
        assertThrows(IllegalArgumentException.class, () -> new CoilCount(2001));
        assertThrows(IllegalArgumentException.class, () -> new IPAddress("not-an-ip"));
        assertThrows(IllegalArgumentException.class, () -> new SerialPortName(" "));
    }

    @Test
    void boundsTemperatureAndRejectsNonFiniteValues() {
        assertEquals(-273.15, new TemperatureCelsius(-273.15).value());
        assertEquals(1000.0, new TemperatureCelsius(1000.0).value());
        assertThrows(IllegalArgumentException.class, () -> new TemperatureCelsius(-273.16));
        assertThrows(IllegalArgumentException.class, () -> new TemperatureCelsius(Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> new TemperatureCelsius(Double.POSITIVE_INFINITY));
    }
}