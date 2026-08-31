package io.oodesigns.modbus.runtime;
import io.oodesigns.modbus.Response; import io.oodesigns.modbus.plugin.DeviceState; import java.util.*;
/** Stores the latest immutable operational state and triggered rules. */
public final class StateManager {
 private DeviceState state=new DeviceState(Map.of()); private Set<String> rules=Set.of();
 public synchronized Response<Void> updateState(DeviceState state, Set<String> rules){try{this.state=Objects.requireNonNull(state);this.rules=Set.copyOf(Objects.requireNonNull(rules));return Response.success(null);}catch(RuntimeException e){return Response.failure(e.getMessage());}}
 public synchronized Response<DeviceState> state(){return Response.success(state);}
 public synchronized Response<Set<String>> triggeredRules(){return Response.success(rules);}
}
