package io.oodesigns.modbus.plugin;
import io.oodesigns.modbus.*; import io.oodesigns.modbus.value.*; import java.nio.file.Path; import java.util.*;
/** Creates self-registering device plugins only after all declared dependencies exist. */
public final class DeviceFactory {
 private final Map<DeviceType,DevicePlugin> plugins; private final ConfigFactory configs; private final Dependencies dependencies;
 public DeviceFactory(ConfigFactory configs, Dependencies dependencies) { this(ServiceLoader.load(DevicePlugin.class),configs,dependencies); }
 public DeviceFactory(Iterable<DevicePlugin> plugins, ConfigFactory configs, Dependencies dependencies) { this.configs=Objects.requireNonNull(configs);this.dependencies=Objects.requireNonNull(dependencies);Map<DeviceType,DevicePlugin> found=new HashMap<>(); plugins.forEach(p->found.put(p.deviceType(),p));this.plugins=Map.copyOf(found); }
 public Response<Device> createDevice(DeviceType type, Path profile) { try { DevicePlugin plugin=plugins.get(Objects.requireNonNull(type)); if(plugin==null)return Response.failure("NOT_REGISTERED: "+type.value()); for(DependencyKey key:plugin.requiredDependencies())if(dependencies.get(key).status()==Status.EXCEPTION)return Response.failure("MISSING_DEPENDENCY: "+key.value()); return configs.load(Objects.requireNonNull(profile)).flatMap(config->plugin.create(config,dependencies)); }catch(RuntimeException e){return Response.failure("Device creation failed: "+e.getMessage());} }
}
