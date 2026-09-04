# Designs

PlantUML designs for a Modbus abstraction layer with a **provider architecture**: new devices and transport adapters are supplied independently and never require changes to existing core code (Open/Closed Principle).

| Diagram | Purpose |
|---|---|
| [`01 Modbus Abstraction.puml`](01%20Modbus%20Abstraction.puml) ([PNG](01%20Modbus%20Abstraction.png)) | `ModbusClient` port, TCP/RTU providers, connection-manager decorator, library adapters |
| [`02 Device Plugin Architecture.puml`](02%20Device%20Plugin%20Architecture.puml) ([PNG](02%20Device%20Plugin%20Architecture.png)) | Device provider contract, `DeviceFactory`, provider catalog, example extensions |
| [`03 Device Runtime and Transformers.puml`](03%20Device%20Runtime%20and%20Transformers.puml) ([PNG](03%20Device%20Runtime%20and%20Transformers.png)) | Polling base device, state management, response transformer strategies |
| [`04 Plugin Discovery Sequence.puml`](04%20Plugin%20Discovery%20Sequence.puml) ([PNG](04%20Plugin%20Discovery%20Sequence.png)) | Provider registration and device creation flow |

## Diagrams

### 01 Modbus Abstraction

![Modbus Abstraction](01%20Modbus%20Abstraction.png)

### 02 Device Plugin Architecture

![Device Plugin Architecture](02%20Device%20Plugin%20Architecture.png)

### 03 Device Runtime and Transformers

![Device Runtime and Transformers](03%20Device%20Runtime%20and%20Transformers.png)

### 04 Plugin Discovery Sequence

![Plugin Discovery Sequence](04%20Plugin%20Discovery%20Sequence.png)

## Key decisions

- **Provider registration** is an architectural boundary. The runtime receives device, transport, and transformer providers from a provider catalog; the catalog's discovery mechanism is an implementation detail.
- A Java deployment may implement the provider catalog with `ServiceLoader`, dependency injection, explicit configuration, or another mechanism. That choice does not belong in the domain design.
- **`ModbusClient`** is the only Modbus API devices see. The underlying library (e.g. digitalpetri/modbus) sits behind an adapter, so it can be swapped without touching device code — same role `ModbusPYClient`/pymodbus played in the POC.
- **`Response<T>` / `DeviceResponse`** carry status + details instead of throwing, mirroring the POC's `Response`/`DeviceStatus` pattern, including the exception-cascade strategy.
- **Dependency validation**: `DevicePlugin.requiredDependencies()` is checked before creation, matching the POC `DeviceFactory` tests (not registered / missing dependency / valid).

## Rendering

PNGs live next to their sources in this folder (`designs/NN Name.png`) and are the rendered
output of the matching `.puml` file — edit the `.puml`, never the `.png`.

Render locally with Graphviz installed and the PlantUML jar (1.2024.7 or newer, which bundles
the `spacelab` theme so no network access is required):

```bash
java -jar plantuml.jar -failfast2 -tpng "designs/"*.puml
```

The `plantuml` package shipped by older distributions (e.g. 1.2020.02) does not bundle the
`spacelab` theme and will fail to render; use the jar from the
[PlantUML releases](https://github.com/plantuml/plantuml/releases) instead. The IntelliJ/VS Code
PlantUML plugins work too.

The [`PlantUML diagrams` workflow](../.github/workflows/plantuml.yml) re-renders the diagrams
whenever a `designs/*.puml` file changes: it fails a pull request whose PNGs are stale, and
commits the refreshed PNGs when the change lands on `main`.
