package com.oodesigns.devicecomms.domain.device;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import com.oodesigns.devicecomms.domain.Response;
import com.oodesigns.devicecomms.domain.communication.CommunicationClientRegistry;
import com.oodesigns.devicecomms.domain.value.DependencyKey;
import com.oodesigns.devicecomms.domain.value.DeviceType;
import com.oodesigns.devicecomms.testkit.TestDevice;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class DeviceFactoryTest {
    private final Dependencies dependencies = new Dependencies(
            new CommunicationClientRegistry(List.of()), Map.of());

    @Test
    void discoversAndCreatesDevicePlugin() {
        final DeviceFactory factory = new DeviceFactory(new ServiceLoaderDeviceProviderCatalog(),
                type -> Response.success(new ConfigLoader(Map.of("profile", type.value()))), dependencies);

        final var response = factory.createDevice(new DeviceType("test-device"));

        assertEquals(Response.Status.OK, response.status());
        assertInstanceOf(TestDevice.class, response.value());
    }

    @Test
    void returnsFailureForUnknownDeviceAndMissingDependency() {
        final DeviceFactory discovered = new DeviceFactory(new ServiceLoaderDeviceProviderCatalog(),
                ignored -> Response.success(new ConfigLoader(Map.of())), dependencies);
        final DevicePlugin missingDependencyPlugin = new DevicePlugin() {
            @Override
            public DeviceType deviceType() {
                return new DeviceType("needs-extra");
            }

            @Override
            public Set<DependencyKey> requiredDependencies() {
                return Set.of(new DependencyKey("extra"));
            }

            @Override
            public Response<Device> create(final ConfigLoader config, final Dependencies deps) {
                return Response.success(new TestDevice(config));
            }
        };
        final DeviceFactory missing = new DeviceFactory(List.of(missingDependencyPlugin),
                ignored -> Response.success(new ConfigLoader(Map.of())), dependencies);

        assertEquals(Response.Status.EXCEPTION, discovered.createDevice(new DeviceType("unknown")).status());
        assertEquals(Response.Status.EXCEPTION, missing.createDevice(new DeviceType("needs-extra")).status());
    }
}