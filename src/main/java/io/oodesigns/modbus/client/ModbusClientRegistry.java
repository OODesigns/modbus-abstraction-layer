package io.oodesigns.modbus.client;
import io.oodesigns.modbus.Response;
import java.util.*;
/** Discovers transport implementations without coupling callers to adapters. */
public final class ModbusClientRegistry {
 private final Map<TransportType, ModbusClientFactory> factories;
 public ModbusClientRegistry() { this(ServiceLoader.load(ModbusClientFactory.class)); }
 public ModbusClientRegistry(Iterable<ModbusClientFactory> factories) { Objects.requireNonNull(factories); Map<TransportType, ModbusClientFactory> found = new EnumMap<>(TransportType.class); factories.forEach(factory -> found.put(factory.transport(), factory)); this.factories = Map.copyOf(found); }
 public Response<ModbusClient> get(TransportType transport, ConnectionSettings settings) { try { var factory = factories.get(Objects.requireNonNull(transport)); return factory == null ? Response.failure("NOT_REGISTERED: " + transport) : factory.create(Objects.requireNonNull(settings)); } catch (RuntimeException exception) { return Response.failure("Client creation failed: " + exception.getMessage()); } }
}
