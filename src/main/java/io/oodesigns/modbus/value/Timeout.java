package io.oodesigns.modbus.value;
import java.time.Duration;
public record Timeout(Duration value) { public Timeout { if (value == null || value.isZero() || value.isNegative()) throw new IllegalArgumentException("timeout must be positive"); } }
