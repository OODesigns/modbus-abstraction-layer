package io.oodesigns.modbus.value;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Connection value objects")
class ConnectionValueObjectsTest {

    @Nested
    @DisplayName("IPAddress")
    class IPAddressTest {

        @ParameterizedTest
        @ValueSource(strings = {"0.0.0.0", "192.168.1.10", "255.255.255.255"})
        void acceptsValidDottedQuads(String candidate) {
            assertEquals(candidate, new IPAddress(candidate).value());
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "  ", "192.168.1", "256.1.1.1", "192.168.1.-1", "1.2.3.4.5", "host name"})
        void rejectsInvalidAddresses(String candidate) {
            assertThrows(IllegalArgumentException.class, () -> new IPAddress(candidate));
        }

        @Test
        void rejectsNull() {
            assertThrows(IllegalArgumentException.class, () -> new IPAddress(null));
        }
    }

    @Nested
    @DisplayName("Port")
    class PortTest {

        @ParameterizedTest
        @ValueSource(ints = {1, 502, 65535})
        void acceptsPortsWithinRange(int candidate) {
            assertEquals(candidate, new Port(candidate).value());
        }

        @ParameterizedTest
        @ValueSource(ints = {0, -1, 65536})
        void rejectsPortsOutsideRange(int candidate) {
            assertThrows(IllegalArgumentException.class, () -> new Port(candidate));
        }
    }

    @Nested
    @DisplayName("SerialPortName")
    class SerialPortNameTest {

        @ParameterizedTest
        @ValueSource(strings = {"/dev/ttyUSB0", "/dev/ttyS1", "COM1", "COM12"})
        void acceptsKnownSerialPortNames(String candidate) {
            assertEquals(candidate, new SerialPortName(candidate).value());
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "ttyUSB0", "COM", "/dev/"})
        void rejectsUnusableSerialPortNames(String candidate) {
            assertThrows(IllegalArgumentException.class, () -> new SerialPortName(candidate));
        }
    }

    @Nested
    @DisplayName("BaudRate")
    class BaudRateTest {

        @ParameterizedTest
        @ValueSource(ints = {1200, 9600, 19200, 115200})
        void acceptsStandardBaudRates(int candidate) {
            assertEquals(candidate, new BaudRate(candidate).value());
        }

        @ParameterizedTest
        @ValueSource(ints = {0, -9600, 9601, 250000})
        void rejectsNonStandardBaudRates(int candidate) {
            assertThrows(IllegalArgumentException.class, () -> new BaudRate(candidate));
        }
    }

    @Nested
    @DisplayName("UnitId")
    class UnitIdTest {

        @ParameterizedTest
        @ValueSource(ints = {1, 100, 247})
        void acceptsAddressableUnits(int candidate) {
            assertEquals(candidate, new UnitId(candidate).value());
        }

        @ParameterizedTest
        @ValueSource(ints = {0, -1, 248})
        void rejectsUnitsOutsideModbusRange(int candidate) {
            assertThrows(IllegalArgumentException.class, () -> new UnitId(candidate));
        }
    }

    @Nested
    @DisplayName("Timeout")
    class TimeoutTest {

        @Test
        void acceptsPositiveDurations() {
            assertEquals(Duration.ofSeconds(3), new Timeout(Duration.ofSeconds(3)).value());
            assertEquals(1L, new Timeout(Duration.ofMillis(1)).value().toMillis());
        }

        @Test
        void rejectsNonPositiveOrMissingDurations() {
            assertThrows(IllegalArgumentException.class, () -> new Timeout(Duration.ZERO));
            assertThrows(IllegalArgumentException.class, () -> new Timeout(Duration.ofSeconds(-1)));
            assertThrows(IllegalArgumentException.class, () -> new Timeout(null));
        }
    }

    @Nested
    @DisplayName("Retries")
    class RetriesTest {

        @ParameterizedTest
        @ValueSource(ints = {0, 3, 10})
        void acceptsSensibleRetryCounts(int candidate) {
            assertEquals(candidate, new Retries(candidate).value());
        }

        @ParameterizedTest
        @ValueSource(ints = {-1, 11})
        void rejectsUnreasonableRetryCounts(int candidate) {
            assertThrows(IllegalArgumentException.class, () -> new Retries(candidate));
        }
    }

    @Nested
    @DisplayName("PollInterval")
    class PollIntervalTest {

        @Test
        void acceptsPositiveIntervalsOnly() {
            assertEquals(Duration.ofMillis(250), new PollInterval(Duration.ofMillis(250)).value());
            assertThrows(IllegalArgumentException.class, () -> new PollInterval(Duration.ZERO));
            assertThrows(IllegalArgumentException.class, () -> new PollInterval(null));
        }
    }

    @Nested
    @DisplayName("Identifiers")
    class IdentifierTest {

        @Test
        void deviceTypeIsANonBlankIdentifier() {
            assertEquals("blauberg_mvhr", new DeviceType("blauberg_mvhr").value());
            assertThrows(IllegalArgumentException.class, () -> new DeviceType(" "));
            assertThrows(IllegalArgumentException.class, () -> new DeviceType(null));
            assertThrows(IllegalArgumentException.class, () -> new DeviceType("has space"));
        }

        @Test
        void dependencyKeyIsANonBlankIdentifier() {
            assertEquals("modbus_registry", new DependencyKey("modbus_registry").value());
            assertThrows(IllegalArgumentException.class, () -> new DependencyKey(""));
            assertThrows(IllegalArgumentException.class, () -> new DependencyKey(null));
        }

        @Test
        void sensorTypeIsANonBlankIdentifier() {
            assertEquals("temperature", new SensorType("temperature").value());
            assertThrows(IllegalArgumentException.class, () -> new SensorType(""));
            assertThrows(IllegalArgumentException.class, () -> new SensorType(null));
        }
    }
}
