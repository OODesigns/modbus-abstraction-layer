package com.oodesigns.devicecomms.device.waveshare.analogoutput;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.oodesigns.devicecomms.adapter.modbus.ModbusTransportKeys;
import com.oodesigns.devicecomms.device.waveshare.analogoutput.configuration.WaveshareAnalogOutput8CHPlugin;
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
import com.oodesigns.devicecomms.domain.device.DeviceFactory;
import com.oodesigns.devicecomms.domain.device.ServiceLoaderDeviceProviderCatalog;
import com.oodesigns.devicecomms.domain.value.BaudRate;
import com.oodesigns.devicecomms.domain.value.DeviceType;
import com.oodesigns.devicecomms.domain.value.SerialPortName;
import com.oodesigns.devicecomms.domain.value.Timeout;
import com.oodesigns.devicecomms.testkit.InMemoryCommunicationClient;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class WaveshareAnalogOutput8CHPluginTest {
    @Test
    void pluginIsDiscoveredAndCreatesOutputDeviceFromItsRtuProfile() {
        final RecordingFactory factory = new RecordingFactory();
        final ConfigLoader config = new ConfigLoader(Map.of(
                "transport", "modbus-rtu",
                "serialPort", "/dev/ttyUSB0",
                "baudRate", 9600,
                "timeoutMillis", 1000));
        final DeviceFactory deviceFactory = new DeviceFactory(new ServiceLoaderDeviceProviderCatalog(),
                ignored -> Response.success(config), dependencies(factory));

        final Response<Device> result = deviceFactory.createDevice(
                new DeviceType("waveshare-modbus-rtu-analog-output-8ch-b"));

        assertTrue(new ServiceLoaderDeviceProviderCatalog().devicePlugins().stream()
                .anyMatch(WaveshareAnalogOutput8CHPlugin.class::isInstance));
        assertEquals(Response.Status.OK, result.status());
        assertInstanceOf(AnalogOutput8CH.class, result.value());
        assertEquals(new SerialEndpoint(new SerialPortName("/dev/ttyUSB0"), new BaudRate(9600),
                new Timeout(Duration.ofMillis(1000))), factory.settings);
    }

    @Test
    void rejectsOtherTransports() {
        final WaveshareAnalogOutput8CHPlugin plugin = new WaveshareAnalogOutput8CHPlugin();
        final Response<Device> result = plugin.create(new ConfigLoader(Map.of(
                "transport", "modbus-tcp", "serialPort", "/dev/ttyUSB0",
                "baudRate", 9600, "timeoutMillis", 1000)), dependencies(new RecordingFactory()));

        assertEquals(Response.Status.EXCEPTION, result.status());
    }

    @Test
    void rejectsUnsupportedBaudRate() {
        final WaveshareAnalogOutput8CHPlugin plugin = new WaveshareAnalogOutput8CHPlugin();
        final Response<Device> result = plugin.create(new ConfigLoader(Map.of(
                "transport", "modbus-rtu", "serialPort", "/dev/ttyUSB0",
                "baudRate", 10000, "timeoutMillis", 1000)), dependencies(new RecordingFactory()));

        assertEquals(Response.Status.EXCEPTION, result.status());
    }

    private static Dependencies dependencies(final RecordingFactory factory) {
        return new Dependencies(new CommunicationClientRegistry(List.of(factory)), Map.of());
    }

    private static final class RecordingFactory implements CommunicationClientFactory {
        private ConnectionSettings settings;

        @Override
        public TransportKey transport() {
            return ModbusTransportKeys.RTU;
        }

        @Override
        public Response<CommunicationClient> create(final ConnectionSettings settings) {
            this.settings = settings;
            return Response.success(new InMemoryCommunicationClient());
        }
    }
}