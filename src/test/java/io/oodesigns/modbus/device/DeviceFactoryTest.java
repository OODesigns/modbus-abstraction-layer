package io.oodesigns.modbus.device;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.oodesigns.modbus.client.ModbusClientRegistry;
import io.oodesigns.modbus.config.ConfigFactory;
import io.oodesigns.modbus.core.Response;
import io.oodesigns.modbus.core.Status;
import io.oodesigns.modbus.fake.FakeTemperatureSensorPlugin;
import io.oodesigns.modbus.value.DependencyKey;
import io.oodesigns.modbus.value.DeviceType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("DeviceFactory")
class DeviceFactoryTest {

    private static Dependencies withRegistry() {
        return Dependencies.builder()
                .with(Dependencies.MODBUS_REGISTRY, new ModbusClientRegistry())
                .build();
    }

    @Test
    @DisplayName("creates a device for a plugin discovered through the ServiceLoader")
    void createsDiscoveredDevice() {
        DeviceFactory factory = new DeviceFactory(new ConfigFactory());

        Response<Device> response = factory.createDevice(FakeTemperatureSensorPlugin.TYPE, withRegistry());

        assertEquals(Status.OK, response.status(), response.details());
        assertTrue(response.value().isPresent());
    }

    @Test
    @DisplayName("returns a NOT_REGISTERED failure Response for an unknown device type")
    void unknownDeviceTypeProducesFailure() {
        DeviceFactory factory = new DeviceFactory(new ConfigFactory());

        Response<Device> response = factory.createDevice(new DeviceType("unknown_device"), withRegistry());

        assertEquals(Status.EXCEPTION, response.status());
        assertTrue(response.details().contains("NOT_REGISTERED"));
        assertTrue(response.details().contains("unknown_device"));
    }

    @Test
    @DisplayName("returns a failure Response when a required dependency is missing")
    void missingDependencyProducesFailure() {
        DeviceFactory factory = new DeviceFactory(new ConfigFactory());

        Response<Device> response = factory.createDevice(FakeTemperatureSensorPlugin.TYPE, Dependencies.none());

        assertEquals(Status.EXCEPTION, response.status());
        assertTrue(response.details().contains("modbus_registry"));
    }

    @Test
    @DisplayName("returns a failure Response when the device profile cannot be loaded")
    void missingConfigProducesFailure() {
        DeviceFactory factory = new DeviceFactory(new ConfigFactory(new io.oodesigns.modbus.config.ConfigLocation("/no-such-profiles")));

        Response<Device> response = factory.createDevice(FakeTemperatureSensorPlugin.TYPE, withRegistry());

        assertEquals(Status.EXCEPTION, response.status());
    }

    @Test
    @DisplayName("the config factory is a construction precondition and arguments are validated")
    void argumentsAreValidated() {
        assertThrows(IllegalArgumentException.class, () -> new DeviceFactory(null));

        DeviceFactory factory = new DeviceFactory(new ConfigFactory());
        assertEquals(Status.EXCEPTION, factory.createDevice(null, withRegistry()).status());
        assertEquals(Status.EXCEPTION, factory.createDevice(FakeTemperatureSensorPlugin.TYPE, null).status());
    }

    @Test
    @DisplayName("registered device types are visible for diagnostics")
    void registeredTypesAreVisible() {
        assertTrue(new DeviceFactory(new ConfigFactory())
                .registeredTypes()
                .contains(FakeTemperatureSensorPlugin.TYPE));
    }

    @Test
    @DisplayName("dependency keys are value objects")
    void dependencyKeysAreValueObjects() {
        assertEquals(new DependencyKey("modbus_registry"), Dependencies.MODBUS_REGISTRY);
    }
}
