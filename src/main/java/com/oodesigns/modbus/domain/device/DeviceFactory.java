package com.oodesigns.modbus.domain.device;

import com.oodesigns.modbus.domain.Response;
import com.oodesigns.modbus.domain.value.DependencyKey;
import com.oodesigns.modbus.domain.value.DeviceType;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class DeviceFactory {
    private final Map<DeviceType, DevicePlugin> plugins;
    private final ConfigFactory configFactory;
    private final Dependencies dependencies;

    public DeviceFactory(final Collection<DevicePlugin> plugins,
                         final ConfigFactory configFactory,
                         final Dependencies dependencies) {
        Objects.requireNonNull(plugins, "plugins");
        final Map<DeviceType, DevicePlugin> indexed = new HashMap<>();
        for (final DevicePlugin plugin : plugins) {
            Objects.requireNonNull(plugin, "plugin");
            final DeviceType type = Objects.requireNonNull(plugin.deviceType(), "plugin deviceType");
            if (indexed.putIfAbsent(type, plugin) != null) {
                throw new IllegalArgumentException("duplicate device provider: " + type.value());
            }
        }
        this.plugins = Map.copyOf(indexed);
        this.configFactory = Objects.requireNonNull(configFactory, "configFactory");
        this.dependencies = Objects.requireNonNull(dependencies, "dependencies");
    }

    public DeviceFactory(final DeviceProviderCatalog catalog,
                         final ConfigFactory configFactory,
                         final Dependencies dependencies) {
        this(Objects.requireNonNull(catalog, "catalog").devicePlugins(), configFactory, dependencies);
    }

    public Response<Device> createDevice(final DeviceType type) {
        Objects.requireNonNull(type, "type");
        final DevicePlugin plugin = plugins.get(type);
        if (plugin == null) {
            return Response.failure("device provider is not registered: " + type.value());
        }
        try {
            for (final DependencyKey dependency : plugin.requiredDependencies()) {
                if (!dependencies.contains(Objects.requireNonNull(dependency, "required dependency"))) {
                    return Response.failure("required dependency is not available: " + dependency.value());
                }
            }
            final Response<ConfigLoader> config = Objects.requireNonNull(configFactory.load(type), "config result");
            return config.flatMap(loaded -> Objects.requireNonNull(plugin.create(loaded, dependencies), "plugin result"));
        } catch (final RuntimeException exception) {
            return Response.failure("device creation failed: " + exception.getMessage());
        }
    }
}