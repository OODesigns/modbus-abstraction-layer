package io.oodesigns.modbus.plugin;
import io.oodesigns.modbus.Response; import io.oodesigns.modbus.value.DependencyKey; import java.util.*;
public final class Dependencies {
 private final Map<DependencyKey,Object> values;
 public Dependencies(Map<DependencyKey,Object> values) { this.values=Map.copyOf(Objects.requireNonNull(values)); }
 public Response<Object> get(DependencyKey key) { if(key==null)return Response.failure("dependency key is required"); Object dependency=values.get(key); return dependency == null ? Response.failure("MISSING_DEPENDENCY: "+key.value()) : Response.success(dependency); }
}
