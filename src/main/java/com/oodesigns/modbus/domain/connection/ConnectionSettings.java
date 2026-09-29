package com.oodesigns.modbus.domain.connection;

public sealed interface ConnectionSettings permits NetworkEndpoint, SerialEndpoint {
}