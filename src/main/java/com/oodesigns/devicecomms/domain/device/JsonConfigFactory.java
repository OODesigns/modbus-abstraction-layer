package com.oodesigns.devicecomms.domain.device;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oodesigns.devicecomms.domain.Response;
import com.oodesigns.devicecomms.domain.value.DeviceType;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

public final class JsonConfigFactory implements ConfigFactory {
    private static final TypeReference<Map<String, Object>> JSON_OBJECT = new TypeReference<>() { };

    private final Map<DeviceType, Path> profiles;
    private final ObjectMapper objectMapper;

    public JsonConfigFactory(final Map<DeviceType, Path> profiles) {
        this(profiles, new ObjectMapper());
    }

    public JsonConfigFactory(final Map<DeviceType, Path> profiles, final ObjectMapper objectMapper) {
        this.profiles = Map.copyOf(Objects.requireNonNull(profiles, "profiles"));
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper");
    }

    @Override
    public Response<ConfigLoader> load(final DeviceType deviceType) {
        if (deviceType == null) {
            return Response.failure("device type is required");
        }
        final Path path = profiles.get(deviceType);
        if (path == null) {
            return Response.failure("configuration profile is not registered: " + deviceType.value());
        }
        try {
            final Map<String, Object> values = objectMapper.readValue(Files.readString(path), JSON_OBJECT);
            return Response.success(new ConfigLoader(values));
        } catch (final IOException | RuntimeException exception) {
            final String detail = exception.getMessage();
            return Response.failure("configuration profile could not be loaded: "
                    + (detail == null || detail.isBlank() ? path : detail));
        }
    }
}