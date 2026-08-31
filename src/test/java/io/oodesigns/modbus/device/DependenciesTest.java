package io.oodesigns.modbus.device;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.oodesigns.modbus.client.ModbusClientRegistry;
import io.oodesigns.modbus.core.Response;
import io.oodesigns.modbus.core.Status;
import io.oodesigns.modbus.value.DependencyKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Dependencies")
class DependenciesTest {

    private static final DependencyKey CLOCK = new DependencyKey("clock");

    @Test
    @DisplayName("returns a registered dependency as a success Response")
    void returnsRegisteredDependency() {
        Dependencies dependencies = Dependencies.builder().with(CLOCK, "a-clock").build();

        Response<Object> response = dependencies.get(CLOCK);

        assertEquals(Status.OK, response.status());
        assertEquals("a-clock", response.value().orElseThrow());
    }

    @Test
    @DisplayName("returns a failure Response for an unknown dependency")
    void unknownDependencyProducesFailure() {
        Response<Object> response = Dependencies.none().get(CLOCK);

        assertEquals(Status.EXCEPTION, response.status());
        assertTrue(response.details().contains("clock"));
    }

    @Test
    @DisplayName("exposes the Modbus registry with its own intent-revealing accessor")
    void exposesModbusRegistry() {
        ModbusClientRegistry registry = new ModbusClientRegistry();
        Dependencies dependencies = Dependencies.builder()
                .with(Dependencies.MODBUS_REGISTRY, registry)
                .build();

        assertEquals(registry, dependencies.modbusRegistry().value().orElseThrow());
        assertEquals(Status.EXCEPTION, Dependencies.none().modbusRegistry().status());
    }

    @Test
    @DisplayName("reports whether a key is provided without ever throwing")
    void reportsAvailability() {
        Dependencies dependencies = Dependencies.builder().with(CLOCK, "a-clock").build();

        assertTrue(dependencies.provides(CLOCK));
        assertEquals(false, dependencies.provides(new DependencyKey("missing")));
        assertEquals(false, dependencies.provides(null));
    }

    @Test
    @DisplayName("a dependency key and value are construction preconditions")
    void buildingIsValidated() {
        assertThrows(IllegalArgumentException.class, () -> Dependencies.builder().with(null, "x").build());
        assertThrows(IllegalArgumentException.class, () -> Dependencies.builder().with(CLOCK, null).build());
    }
}
