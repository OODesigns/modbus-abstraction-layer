package com.oodesigns.devicecomms.device.blauberg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.oodesigns.devicecomms.adapter.modbus.ModbusTransportKeys;
import com.oodesigns.devicecomms.device.blauberg.configuration.BlaubergMVHRPlugin;
import com.oodesigns.devicecomms.domain.Response;
import com.oodesigns.devicecomms.domain.communication.CommunicationClient;
import com.oodesigns.devicecomms.domain.communication.CommunicationClientFactory;
import com.oodesigns.devicecomms.domain.communication.CommunicationClientRegistry;
import com.oodesigns.devicecomms.domain.communication.TransportKey;
import com.oodesigns.devicecomms.domain.connection.ConnectionSettings;
import com.oodesigns.devicecomms.domain.connection.NetworkEndpoint;
import com.oodesigns.devicecomms.domain.connection.SerialEndpoint;
import com.oodesigns.devicecomms.domain.device.ConfigLoader;
import com.oodesigns.devicecomms.domain.device.Dependencies;
import com.oodesigns.devicecomms.domain.device.Device;
import com.oodesigns.devicecomms.domain.device.DeviceFactory;
import com.oodesigns.devicecomms.domain.device.ServiceLoaderDeviceProviderCatalog;
import com.oodesigns.devicecomms.domain.value.BaudRate;
import com.oodesigns.devicecomms.domain.value.IPAddress;
import com.oodesigns.devicecomms.domain.value.Port;
import com.oodesigns.devicecomms.domain.value.SerialPortName;
import com.oodesigns.devicecomms.domain.value.Timeout;
import com.oodesigns.devicecomms.domain.value.DeviceType;
import com.oodesigns.devicecomms.testkit.InMemoryCommunicationClient;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class BlaubergMVHRPluginTest {
    @Test
    void createsDeviceWithNetworkEndpointAndTcpTransport() {
        final RecordingFactory factory = new RecordingFactory(ModbusTransportKeys.TCP);
        final BlaubergMVHRPlugin plugin = new BlaubergMVHRPlugin();
        final Dependencies dependencies = dependencies(factory);
        final ConfigLoader config = new ConfigLoader(Map.of(
                "transport", "modbus-tcp",
                "host", "192.0.2.10",
                "port", 502,
                "timeoutMillis", 1500));

        final Response<Device> result = plugin.create(config, dependencies);

        assertEquals(Response.Status.OK, result.status());
        assertInstanceOf(BlaubergMVHR.class, result.value());
        assertEquals(new NetworkEndpoint(new IPAddress("192.0.2.10"), new Port(502),
                new Timeout(Duration.ofMillis(1500))), factory.settings);
    }

    @Test
    void createsDeviceWithSerialEndpointAndRtuTransport() {
        final RecordingFactory factory = new RecordingFactory(ModbusTransportKeys.RTU);
        final BlaubergMVHRPlugin plugin = new BlaubergMVHRPlugin();
        final Dependencies dependencies = dependencies(factory);
        final ConfigLoader config = new ConfigLoader(Map.of(
                "transport", "modbus-rtu",
                "serialPort", "/dev/ttyUSB0",
                "baudRate", 9600,
                "timeoutMillis", 1000));

        final Response<Device> result = plugin.create(config, dependencies);

        assertEquals(Response.Status.OK, result.status());
        assertInstanceOf(BlaubergMVHR.class, result.value());
        assertEquals(new SerialEndpoint(new SerialPortName("/dev/ttyUSB0"), new BaudRate(9600),
                new Timeout(Duration.ofMillis(1000))), factory.settings);
    }

        @Test
        void pluginIsDiscoveredByTheDeviceProviderCatalog() {
        final boolean discovered = new ServiceLoaderDeviceProviderCatalog().devicePlugins().stream()
            .anyMatch(BlaubergMVHRPlugin.class::isInstance);

        assertTrue(discovered);
        }

    @Test
    void deviceFactoryCreatesBlaubergFromItsRegisteredProfile() {
        final RecordingFactory transportFactory = new RecordingFactory(ModbusTransportKeys.TCP);
        final ConfigLoader config = new ConfigLoader(Map.of(
                "transport", "modbus-tcp",
                "host", "192.0.2.10",
                "port", 502,
                "timeoutMillis", 1500));
        final DeviceFactory deviceFactory = new DeviceFactory(new ServiceLoaderDeviceProviderCatalog(),
                ignored -> Response.success(config), dependencies(transportFactory));

        final Response<Device> result = deviceFactory.createDevice(new DeviceType("blauberg-mvhr"));

        assertEquals(Response.Status.OK, result.status());
        assertInstanceOf(BlaubergMVHR.class, result.value());
    }
    @Test
    void returnsFailureForInvalidNetworkConfigurationInsteadOfThrowing() {
        final RecordingFactory factory = new RecordingFactory(ModbusTransportKeys.TCP);
        final BlaubergMVHRPlugin plugin = new BlaubergMVHRPlugin();
        final ConfigLoader config = new ConfigLoader(Map.of(
                "transport", "modbus-tcp",
                "host", "not-an-ip",
                "port", 502,
                "timeoutMillis", 1000));

        final Response<Device> result = plugin.create(config, dependencies(factory));

        assertEquals(Response.Status.EXCEPTION, result.status());
    }

    @Test
    void rejectsOversizedNetworkPortInsteadOfNarrowingIt() {
        final RecordingFactory factory = new RecordingFactory(ModbusTransportKeys.TCP);
        final BlaubergMVHRPlugin plugin = new BlaubergMVHRPlugin();
        final ConfigLoader config = new ConfigLoader(Map.of(
                "transport", "modbus-tcp",
                "host", "192.0.2.10",
                "port", 4_294_967_798L,
                "timeoutMillis", 1000));

        final Response<Device> result = plugin.create(config, dependencies(factory));

        assertEquals(Response.Status.EXCEPTION, result.status());
        assertEquals(null, factory.settings);
    }

    private static Dependencies dependencies(final RecordingFactory factory) {
        return new Dependencies(new CommunicationClientRegistry(List.of(factory)), Map.of());
    }

    private static final class RecordingFactory implements CommunicationClientFactory {
        private final TransportKey transport;
        private ConnectionSettings settings;

        private RecordingFactory(final TransportKey transport) {
            this.transport = transport;
        }

        @Override
        public TransportKey transport() {
            return transport;
        }

        @Override
        public Response<CommunicationClient> create(final ConnectionSettings settings) {
            this.settings = settings;
            return Response.success(new InMemoryCommunicationClient());
        }
    }
}