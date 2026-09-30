package com.oodesigns.devicecomms.device.waveshare.analoginput;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.oodesigns.devicecomms.adapter.modbus.ModbusTransportKeys;
import com.oodesigns.devicecomms.device.waveshare.analoginput.configuration.WaveshareAnalogInput8CHPlugin;
import com.oodesigns.devicecomms.domain.Response;
import com.oodesigns.devicecomms.domain.communication.CommunicationClient;
import com.oodesigns.devicecomms.domain.communication.CommunicationClientFactory;
import com.oodesigns.devicecomms.domain.communication.CommunicationClientRegistry;
import com.oodesigns.devicecomms.domain.communication.TransportKey;
import com.oodesigns.devicecomms.domain.connection.ConnectionSettings;
import com.oodesigns.devicecomms.domain.connection.SerialEndpoint;
import com.oodesigns.devicecomms.domain.device.ConfigLoader;
import com.oodesigns.devicecomms.domain.device.Dependencies;
import com.oodesigns.devicecomms.domain.device.Device;
import com.oodesigns.devicecomms.domain.device.ServiceLoaderDeviceProviderCatalog;
import com.oodesigns.devicecomms.domain.value.BaudRate;
import com.oodesigns.devicecomms.domain.value.SerialPortName;
import com.oodesigns.devicecomms.domain.value.Timeout;
import com.oodesigns.devicecomms.testkit.InMemoryCommunicationClient;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class WaveshareAnalogInput8CHPluginTest {
    @Test
    void pluginIsDiscoveredByTheServiceLoaderCatalog() {
        final boolean discovered = new ServiceLoaderDeviceProviderCatalog().devicePlugins().stream()
                .anyMatch(WaveshareAnalogInput8CHPlugin.class::isInstance);

        assertTrue(discovered);
    }

    @Test
    void createsSensorReaderWithRtuEndpointAndSelectedHardwareRevision() {
        final RecordingFactory factory = new RecordingFactory(ModbusTransportKeys.RTU);
        final WaveshareAnalogInput8CHPlugin plugin = new WaveshareAnalogInput8CHPlugin();
        final Dependencies dependencies = new Dependencies(
                new CommunicationClientRegistry(List.of(factory)), Map.of());
        final ConfigLoader config = new ConfigLoader(Map.of(
                "transport", "modbus-rtu",
                "serialPort", "/dev/ttyUSB0",
                "baudRate", 115200,
                "timeoutMillis", 1000,
                "moduleRevision", "B"));

        final Response<Device> result = plugin.create(config, dependencies);

        assertEquals(Response.Status.OK, result.status());
        assertInstanceOf(AnalogInput8CH.class, result.value());
        assertEquals(new SerialEndpoint(new SerialPortName("/dev/ttyUSB0"), new BaudRate(115200),
                new Timeout(Duration.ofMillis(1000))), factory.settings);
    }

    @Test
    void returnsFailureForAnUnsupportedModuleRevision() {
        final RecordingFactory factory = new RecordingFactory(ModbusTransportKeys.RTU);
        final WaveshareAnalogInput8CHPlugin plugin = new WaveshareAnalogInput8CHPlugin();
        final Dependencies dependencies = new Dependencies(
                new CommunicationClientRegistry(List.of(factory)), Map.of());
        final ConfigLoader config = new ConfigLoader(Map.of(
                "transport", "modbus-rtu",
                "serialPort", "/dev/ttyUSB0",
                "baudRate", 9600,
                "timeoutMillis", 1000,
                "moduleRevision", "C"));

        final Response<Device> result = plugin.create(config, dependencies);

        assertEquals(Response.Status.EXCEPTION, result.status());
        assertEquals(null, factory.settings);
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