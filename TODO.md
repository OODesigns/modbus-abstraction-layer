# Implementation TODO — Core Design (from `designs/` diagrams)

Tracking list for implementing the core Modbus abstraction layer. Task session: https://github.com/OODesigns/modbus-abstraction-layer/tasks/2837b062-e445-454e-ba28-3154880ed26b

## Design rules (apply everywhere)

- [x] Exceptions thrown ONLY in constructors (design-by-contract preconditions)
- [x] All other methods return `Response<T>` (Success/Failure) — never throw
- [x] Async APIs return `CompletableFuture<Response<T>>` that never completes exceptionally
- [x] Raw values accepted only in value-object constructors; member functions take validated value objects only
- [x] Intent-driven, role-focused interfaces (ISP); no nulls in APIs
- [x] TDD: JUnit 5 coverage for functional core and boundary contracts

## 1. Foundations

- [x] `Response<T>` sealed Success/Failure with `status`, `details`, `value` + `map`/`flatMap`/`fold`
- [x] Exception-cascade behaviour: failure Responses pass through chains unchanged
- [ ] Value objects with construction-time validation:
  - [x] `IPAddress`, `Port`, `SerialPortName`, `BaudRate`, `UnitId`, `Timeout`, `Retries`
  - [x] Modbus `StartAddress` (0–65535), `RegisterCount` (1–125), `CoilCount` (1–2000)
  - [x] `DeviceType`, `DependencyKey`, `SensorType`
  - [x] `TemperatureCelsius` with low/high range validation

## 2. Modbus abstraction (diagram 01)

- [x] `ModbusClient` interface (connect, read/write coils & registers, isConnected)
- [x] `TransportType` enum (TCP, RTU)
- [x] `ModbusClientFactory` SPI + ServiceLoader discovery
- [x] `ModbusClientRegistry` returning `Response<ModbusClient>` (failure when transport not registered)
- [x] `ConnectionSettings` composed only of value objects
- [x] `ConnectionManager` decorator: retry/reconnect honouring `Retries`

## 3. Device plugin architecture (diagram 02)

- [x] `Device` interface (open/read/close)
- [x] `DevicePlugin` SPI (`deviceType()`, `requiredDependencies()`, `create(config, deps)`)
- [x] `DeviceFactory`: ServiceLoader discovery, dependency validation, NOT_REGISTERED / missing-dependency failure Responses
- [x] `Dependencies`, `ConfigFactory`, `ConfigLoader` (JSON device profiles)

## 4. Device runtime & transformers (diagram 03)

- [x] `AbstractModbusDevice` (connect → running; failure marks stopped + START_FAILURE rule)
- [x] `StateManager`
- [x] `ResponseTransformer<R, V>` SPI + `ResponseTransformerFactory` (ServiceLoader)
- [x] `TemperatureTransformer` producing `TemperatureCelsius`

## 5. Wiring & delivery

- [x] `META-INF/services` entries for at least one fake factory and plugin (used by tests)
- [x] Fake/in-memory `ModbusClient` for tests (no digitalpetri adapters yet)
- [x] README/docs section on the Result-based error model and value-object convention
- [x] GitHub Actions CI workflow running build + tests on push/PR
