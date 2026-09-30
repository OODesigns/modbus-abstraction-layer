# Designs

PlantUML designs for a device communication abstraction layer with a **provider architecture**: new devices and transport adapters are supplied independently and never require changes to existing core code (Open/Closed Principle). Each diagram is deliberately small and focused on one concern — cross-references point to the diagram that defines a type shown only by name.

| Diagram | Purpose |
|---|---|
| [`01 Communication Client Core.puml`](01%20Communication%20Client%20Core.puml) ([PNG](01%20Communication%20Client%20Core.png)) | The `CommunicationClient` port and `PointAddress`/`PointSnapshot` model — the only communication API a device sees |
| [`02 Connection Provisioning.puml`](02%20Connection%20Provisioning.puml) ([PNG](02%20Connection%20Provisioning.png)) | `CommunicationClientFactory`/`CommunicationClientRegistry` discovery, `NetworkEndpoint`/`SerialEndpoint` connection settings, `ConnectionManager` decorator |
| [`03 Modbus Adapter.puml`](03%20Modbus%20Adapter.puml) ([PNG](03%20Modbus%20Adapter.png)) | First protocol implementation, entirely behind `adapter: modbus` |
| [`04 Device Contract.puml`](04%20Device%20Contract.puml) ([PNG](04%20Device%20Contract.png)) | `Device`/`DeviceQuery`/`DeviceCommand` — the only API an application author sees |
| [`05 Device Plugin Factory.puml`](05%20Device%20Plugin%20Factory.puml) ([PNG](05%20Device%20Plugin%20Factory.png)) | `DevicePlugin` provider contract, `DeviceFactory`, provider catalog, dependency validation |
| [`06 Example Device Extensions.puml`](06%20Example%20Device%20Extensions.puml) ([PNG](06%20Example%20Device%20Extensions.png)) | Four concrete device plugins proving the abstraction generalizes (MVHR, temp sensor, relay, analog output) |
| [`07 Device Composable Behaviors.puml`](07%20Device%20Composable%20Behaviors.puml) ([PNG](07%20Device%20Composable%20Behaviors.png)) | `Polling`/`StatefulDevice` opt-in behaviors composed by a device, instead of one base class |
| [`08 Response Transformers.puml`](08%20Response%20Transformers.puml) ([PNG](08%20Response%20Transformers.png)) | `ResponseTransformer` strategy for converting raw point values into typed domain values |
| [`09 Startup Provider Registration.puml`](09%20Startup%20Provider%20Registration.puml) ([PNG](09%20Startup%20Provider%20Registration.png)) | Sequence: provider catalog populates the device and transport registries |
| [`10 Runtime Device Creation.puml`](10%20Runtime%20Device%20Creation.puml) ([PNG](10%20Runtime%20Device%20Creation.png)) | Sequence: creating and starting a device on request |
| [`11 MVHR Modbus Device.puml`](11%20MVHR%20Modbus%20Device.puml) ([PNG](11%20MVHR%20Modbus%20Device.png)) | MVHR plugin, profile, signal map and the shared Modbus TCP/RTU client boundary |

## Diagrams

### 01 Communication Client Core

![Communication Client Core](01%20Communication%20Client%20Core.png)

### 02 Connection Provisioning

![Connection Provisioning](02%20Connection%20Provisioning.png)

### 03 Modbus Adapter

![Modbus Adapter](03%20Modbus%20Adapter.png)

### 04 Device Contract

![Device Contract](04%20Device%20Contract.png)

### 05 Device Plugin Factory

![Device Plugin Factory](05%20Device%20Plugin%20Factory.png)

### 06 Example Device Extensions

![Example Device Extensions](06%20Example%20Device%20Extensions.png)

### 07 Device Composable Behaviors

![Device Composable Behaviors](07%20Device%20Composable%20Behaviors.png)

### 08 Response Transformers

![Response Transformers](08%20Response%20Transformers.png)

### 09 Startup Provider Registration

![Startup Provider Registration](09%20Startup%20Provider%20Registration.png)

### 10 Runtime Device Creation

![Runtime Device Creation](10%20Runtime%20Device%20Creation.png)

### 11 MVHR Modbus Device

![MVHR Modbus Device](11%20MVHR%20Modbus%20Device.png)

## Key decisions

- **Provider registration** is an architectural boundary. The runtime receives device, transport, and transformer providers from a provider catalog; the catalog's discovery mechanism is an implementation detail.
- A Java deployment may implement the provider catalog with `ServiceLoader`, dependency injection, explicit configuration, or another mechanism. That choice does not belong in the domain design.
- **`CommunicationClient`** is the only communication API devices see, expressed purely in `PointAddress`/`PointSnapshot` terms — no protocol vocabulary (registers, coils, tags, ...) leaks past it. Modbus (or any other protocol) sits entirely behind an `adapter: <protocol>` package, so it can be swapped without touching device code — same role `ModbusPYClient`/pymodbus played in the POC.
- **`ConnectionSettings` splits into reusable `NetworkEndpoint`/`SerialEndpoint` shapes**, not protocol-specific ones. Any IP-based adapter (Modbus TCP today, a future BACnet/IP) shares `NetworkEndpoint`; any serial-based adapter (Modbus RTU today, a future DNP3-serial) shares `SerialEndpoint`. Network-vs-serial is a connection-settings concern, not part of the `CommunicationClient`/`PointAddress` hierarchy itself.
- **Device writes use the Command pattern.** A caller sends a typed `DeviceCommand` such as `SetPower` or `SetFanSpeed`; the device translates that command into protocol-level point writes. Callers do not address Modbus registers directly.
- **Device reads use the Command pattern (read side).** A caller sends a typed `DeviceQuery` such as `ReadTemperatures` or `ReadAlarms`; `ReadSnapshot` is an explicit query when a complete state view is needed. The device chooses the required protocol reads and callers never address registers directly.
- **`Response<T>` / `DeviceResponse`** carry status + details instead of throwing, mirroring the POC's `Response`/`DeviceStatus` pattern. Exception cascading is inherent to `Response.map()`/`flatMap()` itself, not a separate strategy class.
- **`ResponseTransformer<R, V>`** uses the Strategy pattern for converting raw point values into typed domain values (e.g. `TemperatureTransformer`).
- **Device runtime behavior is composed, not inherited from one base class.** `Polling` and `StatefulDevice` are small opt-in interfaces (`Polling` provides a default `startPolling()` loop); a device implements only the ones it needs instead of extending a single do-everything abstract class — a future event-driven, non-polling device just skips `Polling`.
- **Dependency validation**: `DevicePlugin.requiredDependencies()` is checked before creation, matching the POC `DeviceFactory` tests (not registered / missing dependency / valid).

## Operating the MVHR

The [MVHR control overview](../docs/reference/mvhr-control-overview.md) explains
the S21 power, fan and weekly-temperature controls, the intended application API,
and which parts remain design-only.

## Rendering

PNGs live next to their sources in this folder (`designs/NN Name.png`) and are the rendered
output of the matching `.puml` file — edit the `.puml`, never the `.png`.

Render all diagrams from the repository root with:

```bash
tools/render-puml.sh
```

The script uses `plantuml.jar` in the repository root by default. Set `PLANTUML_JAR`
to use a jar stored elsewhere. Install Java and Graphviz first; use PlantUML 1.2024.7
or newer so the `spacelab` theme is available locally.

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
