package io.oodesigns.modbus.plugin;
import java.util.Map;
public record DeviceState(Map<String, Object> values) { public DeviceState { values=Map.copyOf(values); } }
