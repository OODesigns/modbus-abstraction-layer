# Implementation TODO — Core Design (from `designs/` diagrams)

Tracking list for implementing the core Modbus abstraction layer. Task session: https://github.com/OODesigns/modbus-abstraction-layer/tasks/2837b062-e445-454e-ba28-3154880ed26b

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
- [ ] Value objects with construction-time validation:
  - [ ] `IPAddress`, `Port`, `SerialPortName`, `BaudRate`, `UnitId`, `Timeout`, `Retries`
  - [ ] Modbus `StartAddress` (0–65535), `RegisterCount` (1–125), `CoilCount` (1–2000)
  - [ ] `DeviceType`, `DependencyKey`, `SensorType`
  - [ ] `TemperatureCelsius` with low/high range validation

## 2. Modbus abstraction (diagram 01)

- [ ] `ModbusClient` interface (connect, read/write coils & registers, isConnected)
- [ ] `TransportType` enum (TCP, RTU)
- [ ] `ModbusClientFactory` provider contract + provider-catalog discovery
- [ ] `ModbusClientRegistry` returning `Response<ModbusClient>` (failure when transport not registered)
- [ ] `ConnectionSettings` composed only of value objects
- [ ] `ConnectionManager` decorator: retry/reconnect honouring `Retries`/`Timeout`

## 3. Device plugin architecture (diagram 02)

- [ ] `Device` interface (open/read/close)
- [ ] `DevicePlugin` provider contract (`deviceType()`, `requiredDependencies()`, `create(config, deps)`)
- [ ] `DeviceFactory`: provider-catalog discovery, dependency validation, NOT_REGISTERED / missing-dependency failure Responses
- [ ] `Dependencies`, `ConfigFactory`, `ConfigLoader` (JSON device profiles)

## 4. Device runtime & transformers (diagram 03)

- [ ] `AbstractModbusDevice` (connect → running → poll loop; failure marks stopped + START_FAILURE rule)
- [ ] `StateManager`
- [ ] `ResponseTransformer<R, V>` provider contract + `ResponseTransformerFactory`
- [ ] `TemperatureTransformer` producing `TemperatureCelsius`

## 5. Wiring & delivery

- [ ] `META-INF/services` entries for at least one fake factory and plugin (used by tests)
- [ ] Fake/in-memory `ModbusClient` for tests (no digitalpetri adapters yet)
- [ ] README/docs section on the Result-based error model and value-object convention
- [ ] GitHub Actions CI workflow running build + tests on push/PR
