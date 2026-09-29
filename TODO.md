# Implementation TODO — Core Design (from `designs/` diagrams)

Tracking list for implementing the core device communication abstraction layer. Task session: https://github.com/OODesigns/modbus-abstraction-layer/tasks/2837b062-e445-454e-ba28-3154880ed26b

## Design rules (apply everywhere)

- [ ] Exceptions thrown ONLY in constructors (design-by-contract preconditions)
- [ ] All other methods return `Response<T>` (Success/Failure) — never throw
- [ ] Async APIs return `CompletableFuture<Response<T>>` that never completes exceptionally
- [ ] Raw values accepted only in value-object constructors; member functions take validated value objects only
- [ ] Intent-driven, role-focused interfaces (ISP); no nulls in APIs
- [ ] TDD: failing tests first (JUnit 5), then implementation to green

## 1. Foundations

- [x] `Response<T>` sealed Success/Failure with `status`, `details`, `value` + `map`/`flatMap`/`fold`
- [x] Exception-cascade behaviour: failure Responses pass through chains unchanged
- [x] Value objects with construction-time validation:
  - [x] `IPAddress`, `Port`, `SerialPortName`, `BaudRate`, `UnitId`, `Timeout`, `Retries`
  - [x] Modbus `StartAddress` (0–65535), `RegisterCount` (1–125), `CoilCount` (1–2000)
  - [x] `DeviceType`, `DependencyKey`, `SensorType`
  - [x] `TemperatureCelsius` with low/high range validation

## 2. Communication client core & provisioning (diagrams 01–03)

- [x] `PointAddress<T>` (opaque handle) and `PointSnapshot` (typed read results)
- [x] `CommunicationClient` interface (connect, read(points), write(point, value), isConnected)
- [x] `CommunicationClientFactory` provider contract + provider-catalog discovery, keyed by `TransportKey`
- [x] `CommunicationClientRegistry` returning `Response<CommunicationClient>` (failure when transport not registered)
- [x] `ConnectionSettings` sealed hierarchy: `NetworkEndpoint` (host/port/timeout) and `SerialEndpoint` (port/baud/timeout) shared across protocol adapters, composed only of value objects
- [x] `ConnectionManager` decorator: retry/reconnect honouring `Retries`/`Timeout`
- [x] `adapter: modbus` package: `ModbusPointAddress` (CoilPoint/DiscreteInputPoint/HoldingRegisterPoint/InputRegisterPoint), Modbus `TransportKey`s, digitalpetri-backed `CommunicationClient` implementations

## 3. Device contract & plugin factory (diagrams 04–06)

- [x] `Device` interface (open/read query/execute command/close)
- [x] `DevicePlugin` provider contract (`deviceType()`, `requiredDependencies()`, `create(config, deps)`)
- [x] `DeviceFactory`: provider-catalog discovery, dependency validation, NOT_REGISTERED / missing-dependency failure Responses
- [x] `Dependencies` (`communicationRegistry()`), `ConfigFactory`, `ConfigLoader` (JSON device profiles)

## 4. Device composable behaviors & transformers (diagrams 07–08)

- [x] `Polling` interface (opt-in behavior; default `startPolling()` wires a repeating `pollOnce()`; failure marks stopped + START_FAILURE rule)
- [x] `StatefulDevice` interface (opt-in behavior; exposes `stateManager()`)
- [x] `StateManager`
- [x] `ResponseTransformer<R, V>` provider contract + `ResponseTransformerFactory`
- [x] `TemperatureTransformer` producing `TemperatureCelsius`

## 5. Wiring & delivery

- [x] `META-INF/services` entries for at least one fake factory and plugin (used by tests)
- [x] Fake/in-memory `CommunicationClient` for tests (no digitalpetri adapters yet)
- [x] README/docs section on the Result-based error model and value-object convention
- [x] GitHub Actions CI workflow running build + tests on push/PR
