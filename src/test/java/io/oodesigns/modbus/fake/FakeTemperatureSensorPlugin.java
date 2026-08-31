package io.oodesigns.modbus.fake;

import io.oodesigns.modbus.client.ConnectionSettings;
import io.oodesigns.modbus.client.ModbusClient;
import io.oodesigns.modbus.client.ModbusClientRegistry;
import io.oodesigns.modbus.client.TransportType;
import io.oodesigns.modbus.config.ConfigKey;
import io.oodesigns.modbus.config.ConfigLoader;
import io.oodesigns.modbus.core.Response;
import io.oodesigns.modbus.device.Dependencies;
import io.oodesigns.modbus.device.Device;
import io.oodesigns.modbus.device.DevicePlugin;
import io.oodesigns.modbus.value.DependencyKey;
import io.oodesigns.modbus.value.DeviceType;
import io.oodesigns.modbus.value.IPAddress;
import io.oodesigns.modbus.value.Port;
import io.oodesigns.modbus.value.Retries;
import io.oodesigns.modbus.value.Timeout;
import io.oodesigns.modbus.value.UnitId;
import java.time.Duration;
import java.util.Set;

/** Fake plugin registered through {@code META-INF/services} for the tests. */
public final class FakeTemperatureSensorPlugin implements DevicePlugin {

    public static final DeviceType TYPE = new DeviceType("fake_temp_sensor");

    private static final ConfigKey HOST = new ConfigKey("host");
    private static final ConfigKey PORT = new ConfigKey("port");

    @Override
    public DeviceType deviceType() {
        return TYPE;
    }

    @Override
    public Set<DependencyKey> requiredDependencies() {
        return Set.of(Dependencies.MODBUS_REGISTRY);
    }

    @Override
    public Response<Device> create(ConfigLoader config, Dependencies dependencies) {
        return dependencies.modbusRegistry()
                .flatMap(registry -> client(registry, config))
                .map(FakeSensorDevice::new);
    }

    private Response<ModbusClient> client(ModbusClientRegistry registry, ConfigLoader config) {
        return config.string(HOST).map(IPAddress::new).flatMap(host ->
                config.integer(PORT).map(Port::new).flatMap(port ->
                        registry.get(TransportType.TCP, ConnectionSettings.forTcp(
                                host, port, new UnitId(1), new Timeout(Duration.ofSeconds(1)), new Retries(1)))));
    }
}
