# modbus-abstraction-layer
Java Modbus abstraction layer with a plugin architecture for defining new Modbus devices — design-first with PlantUML

## Error model and values

The core uses `Response<T>` rather than operational exceptions. Operations return either
`Success<T>` (`Status.OK`) or `Failure<T>` (`Status.EXCEPTION`); asynchronous operations
complete normally with a `Response`. `map` and `flatMap` propagate failures unchanged, and
convert mapper exceptions into failure details.

Public domain APIs accept validated value objects rather than primitive transport values.
Create `IPAddress`, `Port`, `UnitId`, addresses, counts, and sensor values at the boundary;
their constructors enforce their documented ranges. Constructors are the sole location where
the core throws for contract violations.

Adapters and device/transformer extensions use Java `ServiceLoader`. The bundled in-memory
Modbus client and device plugin provide a hardware-free integration path.
