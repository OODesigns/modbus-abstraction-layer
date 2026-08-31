package io.oodesigns.modbus.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.oodesigns.modbus.core.Response;
import io.oodesigns.modbus.core.Status;
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

@DisplayName("ModbusClientRegistry")
class ModbusClientRegistryTest {

    private static ConnectionSettings tcpSettings() {
        return ConnectionSettings.forTcp(
                new IPAddress("192.168.1.10"),
                new Port(502),
                new UnitId(1),
                new Timeout(Duration.ofSeconds(1)),
                new Retries(1));
    }

    private static ConnectionSettings rtuSettings() {
        return ConnectionSettings.forRtu(
                new SerialPortName("/dev/ttyUSB0"),
                new BaudRate(9600),
                new UnitId(1),
                new Timeout(Duration.ofSeconds(1)),
                new Retries(1));
    }

    @Test
    @DisplayName("discovers factories through the ServiceLoader")
    void discoversFactoriesViaServiceLoader() {
        Response<ModbusClient> response = new ModbusClientRegistry().get(TransportType.TCP, tcpSettings());

        assertEquals(Status.OK, response.status());
        assertTrue(response.value().isPresent());
    }

    @Test
    @DisplayName("returns a failure Response when the transport has no registered factory")
    void unregisteredTransportProducesFailure() {
        Response<ModbusClient> response = new ModbusClientRegistry().get(TransportType.RTU, rtuSettings());

        assertEquals(Status.EXCEPTION, response.status());
        assertTrue(response.details().contains("RTU"));
    }

    @Test
    @DisplayName("wraps every created client in a ConnectionManager so retries always apply")
    void createdClientsAreManaged() {
        Response<ModbusClient> response = new ModbusClientRegistry().get(TransportType.TCP, tcpSettings());

        assertTrue(response.value().orElseThrow() instanceof ConnectionManager);
    }

    @Test
    @DisplayName("settings must match the requested transport")
    void settingsMustMatchTransport() {
        Response<ModbusClient> response = new ModbusClientRegistry().get(TransportType.RTU, tcpSettings());

        assertEquals(Status.EXCEPTION, response.status());
    }

    @Test
    @DisplayName("collaborators are construction preconditions")
    void collaboratorsAreRequired() {
        ModbusClientRegistry registry = new ModbusClientRegistry();

        assertThrows(IllegalArgumentException.class, () -> new ModbusClientRegistry(null));
        assertEquals(Status.EXCEPTION, registry.get(TransportType.TCP, null).status());
        assertEquals(Status.EXCEPTION, registry.get(null, tcpSettings()).status());
    }
}
