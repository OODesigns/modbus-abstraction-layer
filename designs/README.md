# Designs

PlantUML designs for the Java Modbus abstraction layer with a **plugin architecture**: new devices self-register and never require changes to existing code (Open/Closed Principle).

| Diagram | Purpose |
|---|---|
| `01 Modbus Abstraction.puml` | `ModbusClient` interface, TCP/RTU factories, connection-manager decorator, library adapters |
| `02 Device Plugin Architecture.puml` | `DevicePlugin` SPI, `DeviceFactory` with ServiceLoader discovery, example plugins as separate jars |
| `03 Device Runtime and Transformers.puml` | Polling base device, state management, response transformer strategies |
| `04 Plugin Discovery Sequence.puml` | Startup discovery and device creation flow |

## Key decisions

- **Java `ServiceLoader`** is the self-registration mechanism (`META-INF/services`). Each device plugin is its own module/jar; dropping it on the classpath registers it — the Java equivalent of the Python POC's `@DeviceFactory.register_device` decorator, but with zero central code changes.
- **`ModbusClient`** is the only Modbus API devices see. The underlying library (e.g. digitalpetri/modbus) sits behind an adapter, so it can be swapped without touching device code — same role `ModbusPYClient`/pymodbus played in the POC.
- **`Response<T>` / `DeviceResponse`** carry status + details instead of throwing, mirroring the POC's `Response`/`DeviceStatus` pattern, including the exception-cascade strategy.
- **Dependency validation**: `DevicePlugin.requiredDependencies()` is checked before creation, matching the POC `DeviceFactory` tests (not registered / missing dependency / valid).

## Rendering

Use any PlantUML renderer, e.g. `plantuml designs/*.puml` or the IntelliJ/VS Code PlantUML plugin.
