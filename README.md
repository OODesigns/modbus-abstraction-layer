# device-hardware-comms
Java device hardware communication abstraction layer with a plugin architecture for defining new devices and protocol adapters (Modbus first) — design-first with PlantUML

## Result model and value objects

Operations return `Response<T>` rather than exposing routine failures as exceptions. A successful response carries a value; a failed response carries non-blank details. `map` and `flatMap` pass a failure through unchanged, while asynchronous boundaries use `CompletableFuture<Response<T>>` and normalize transport exceptions into failure responses.

Raw configuration values enter the domain through validated value-object constructors. For example:

```java
var endpoint = new NetworkEndpoint(
	new IPAddress("192.0.2.10"),
	new Port(502),
	new Timeout(Duration.ofSeconds(2)));
```

Constructors reject invalid values immediately. APIs then accept the validated types rather than repeating raw numbers or strings. Modbus addresses and quantities enforce the protocol ranges documented in `TODO.md`.

## Provider setup

`CommunicationClientRegistry`, `DeviceFactory`, and `ResponseTransformerFactory` accept explicit providers or discover providers through `ServiceLoader`. The Modbus adapter exposes TCP and RTU factories; deployments choose a Modbus unit ID by constructing the corresponding factory with a `UnitId`.

Run the unit suite with:

```bash
gradle test
```
