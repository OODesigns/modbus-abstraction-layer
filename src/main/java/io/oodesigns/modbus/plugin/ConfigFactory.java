package io.oodesigns.modbus.plugin;
import io.oodesigns.modbus.Response; import java.nio.file.Path;
public final class ConfigFactory { public Response<ConfigLoader> load(Path profile) { return ConfigLoader.load(profile); } }
