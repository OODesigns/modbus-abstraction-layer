package io.oodesigns.modbus.client;
import io.oodesigns.modbus.value.*;
import java.util.Objects;
/** Immutable connection contract; all values have been validated before use. */
public record ConnectionSettings(IPAddress host, Port port, SerialPortName serialPort, BaudRate baudRate, UnitId unitId, Timeout timeout, Retries retries) {
 public ConnectionSettings { Objects.requireNonNull(host); Objects.requireNonNull(port); Objects.requireNonNull(serialPort); Objects.requireNonNull(baudRate); Objects.requireNonNull(unitId); Objects.requireNonNull(timeout); Objects.requireNonNull(retries); }
}
