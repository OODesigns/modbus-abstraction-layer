package com.oodesigns.devicecomms.domain.connection;

public sealed interface ConnectionSettings permits NetworkEndpoint, SerialEndpoint {
}