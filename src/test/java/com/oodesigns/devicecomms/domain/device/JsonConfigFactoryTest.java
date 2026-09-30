package com.oodesigns.devicecomms.domain.device;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.oodesigns.devicecomms.domain.Response;
import com.oodesigns.devicecomms.domain.value.DeviceType;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class JsonConfigFactoryTest {
    @TempDir
    Path directory;

    @Test
    void loadsJsonProfileAndReturnsFailureForMissingOrMalformedProfile() throws Exception {
        final DeviceType type = new DeviceType("sensor");
        final Path valid = directory.resolve("sensor.json");
        final Path malformed = directory.resolve("malformed.json");
        Files.writeString(valid, "{\"transport\":\"modbus-tcp\",\"address\":7}");
        Files.writeString(malformed, "{invalid-json}");
        final JsonConfigFactory factory = new JsonConfigFactory(Map.of(type, valid,
                new DeviceType("malformed"), malformed));

        final var loaded = factory.load(type);

        assertEquals(Response.Status.OK, loaded.status());
        assertEquals("modbus-tcp", loaded.value().values().get("transport"));
        assertEquals(Response.Status.EXCEPTION, factory.load(new DeviceType("missing")).status());
        assertEquals(Response.Status.EXCEPTION, factory.load(new DeviceType("malformed")).status());
    }
}