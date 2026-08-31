package io.oodesigns.modbus.plugin;
import io.oodesigns.modbus.Response; import io.oodesigns.modbus.value.*; import java.util.Set;
public interface DevicePlugin { DeviceType deviceType(); Set<DependencyKey> requiredDependencies(); Response<Device> create(ConfigLoader config, Dependencies dependencies); }
