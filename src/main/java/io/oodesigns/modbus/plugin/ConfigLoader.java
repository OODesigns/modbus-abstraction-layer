package io.oodesigns.modbus.plugin;
import io.oodesigns.modbus.Response; import java.nio.file.*; import java.util.*; import java.util.regex.*;
/** Immutable JSON object profile. Values are read through intent-specific keys. */
public final class ConfigLoader {
 private static final Pattern PROPERTY=Pattern.compile("\"([^\"]+)\"\\s*:\\s*\"([^\"]*)\"");
 private final Map<String,String> values;
 ConfigLoader(Map<String,String> values) { this.values=Map.copyOf(values); }
 public Response<String> value(String key) { String value=values.get(key); return value == null ? Response.failure("MISSING_CONFIG: "+key) : Response.success(value); }
 static Response<ConfigLoader> parse(String json) { try { Map<String,String> values=new HashMap<>(); Matcher m=PROPERTY.matcher(json); while(m.find()) values.put(m.group(1),m.group(2)); return values.isEmpty()?Response.failure("Invalid JSON profile"):Response.success(new ConfigLoader(values)); } catch(RuntimeException e) { return Response.failure("Invalid JSON profile"); } }
 public static Response<ConfigLoader> load(Path path) { try { return parse(Files.readString(Objects.requireNonNull(path))); } catch(Exception e) { return Response.failure("Unable to load profile: "+e.getMessage()); } }
}
