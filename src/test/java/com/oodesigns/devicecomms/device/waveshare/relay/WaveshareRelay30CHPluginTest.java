package com.oodesigns.devicecomms.device.waveshare.relay;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.oodesigns.devicecomms.adapter.modbus.ModbusTransportKeys;
import com.oodesigns.devicecomms.device.waveshare.relay.configuration.WaveshareRelay30CHPlugin;
import com.oodesigns.devicecomms.domain.Response;
import com.oodesigns.devicecomms.domain.communication.CommunicationClient;
import com.oodesigns.devicecomms.domain.communication.CommunicationClientFactory;
import com.oodesigns.devicecomms.domain.communication.CommunicationClientRegistry;
import com.oodesigns.devicecomms.domain.communication.TransportKey;
import com.oodesigns.devicecomms.domain.connection.ConnectionSettings;
import com.oodesigns.devicecomms.domain.connection.NetworkEndpoint;
import com.oodesigns.devicecomms.domain.device.ConfigLoader;
import com.oodesigns.devicecomms.domain.device.Dependencies;
import com.oodesigns.devicecomms.domain.device.Device;
import com.oodesigns.devicecomms.domain.device.DeviceFactory;
import com.oodesigns.devicecomms.domain.device.ServiceLoaderDeviceProviderCatalog;
import com.oodesigns.devicecomms.domain.value.DeviceType;
import com.oodesigns.devicecomms.domain.value.IPAddress;
import com.oodesigns.devicecomms.domain.value.Port;
import com.oodesigns.devicecomms.domain.value.Timeout;
import com.oodesigns.devicecomms.testkit.InMemoryCommunicationClient;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class WaveshareRelay30CHPluginTest {
    @Test
    void pluginIsDiscoveredAndCreatesRelayDeviceWithTcpEndpoint() {
        final RecordingFactory factory = new RecordingFactory();
        final ConfigLoader config = new ConfigLoader(Map.of(
                "transport", "modbus-tcp", "host", "192.0.2.45", "port", 502, "timeoutMillis", 1500));
        final DeviceFactory deviceFactory = new DeviceFactory(new ServiceLoaderDeviceProviderCatalog(),
                ignored -> Response.success(config), new Dependencies(
                        new CommunicationClientRegistry(List.of(factory)), Map.of()));

        final Response<Device> result = deviceFactory.createDevice(
                new DeviceType("waveshare-modbus-relay-30ch"));

        assertTrue(new ServiceLoaderDeviceProviderCatalog().devicePlugins().stream()
                .anyMatch(WaveshareRelay30CHPlugin.class::isInstance));
        assertEquals(Response.Status.OK, result.status());
        assertInstanceOf(Relay30CH.class, result.value());
        assertEquals(new NetworkEndpoint(new IPAddress("192.0.2.45"), new Port(502),
                new Timeout(Duration.ofMillis(1500))), factory.settings);
    }

    @Test
    void rejectsTransparentRtuProfileUntilTransportSupportsRtuOverTcp() {
        final WaveshareRelay30CHPlugin plugin = new WaveshareRelay30CHPlugin();
        final Response<Device> result = plugin.create(new ConfigLoader(Map.of(
                "transport", "modbus-rtu", "host", "192.0.2.45", "port", 502, "timeoutMillis", 1500)),
                new Dependencies(new CommunicationClientRegistry(List.of(new RecordingFactory())), Map.of()));

        assertEquals(Response.Status.EXCEPTION, result.status());
    }

    private static final class RecordingFactory implements CommunicationClientFactory {
        private ConnectionSettings settings;

        @Override
        public TransportKey transport() {
            return ModbusTransportKeys.TCP;
        }

        @Override
        public Response<CommunicationClient> create(final ConnectionSettings settings) {
            this.settings = settings;
            return Response.success(new InMemoryCommunicationClient());
        }
    }
}