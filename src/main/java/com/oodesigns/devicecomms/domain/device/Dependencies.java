package com.oodesigns.devicecomms.domain.device;

import com.oodesigns.devicecomms.domain.Response;
import com.oodesigns.devicecomms.domain.communication.CommunicationClientRegistry;
import com.oodesigns.devicecomms.domain.value.DependencyKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class Dependencies {
    public static final DependencyKey COMMUNICATION_REGISTRY = new DependencyKey("communication-registry");

    private final CommunicationClientRegistry communicationRegistry;
    private final Map<DependencyKey, Object> values;

    public Dependencies(final CommunicationClientRegistry communicationRegistry,
                        final Map<DependencyKey, ?> values) {
        this.communicationRegistry = Objects.requireNonNull(communicationRegistry, "communicationRegistry");
        Objects.requireNonNull(values, "values");
        final Map<DependencyKey, Object> copied = new HashMap<>();
        values.forEach((key, value) -> copied.put(Objects.requireNonNull(key, "dependency key"),
                Objects.requireNonNull(value, "dependency value")));
        copied.putIfAbsent(COMMUNICATION_REGISTRY, communicationRegistry);
        this.values = Map.copyOf(copied);
    }

    public CommunicationClientRegistry communicationRegistry() {
        return communicationRegistry;
    }

    public Response<Object> get(final DependencyKey key) {
        Objects.requireNonNull(key, "key");
        final Object dependency = values.get(key);
        return dependency == null
                ? Response.failure("required dependency is not available: " + key.value())
                : Response.success(dependency);
    }

    public boolean contains(final DependencyKey key) {
        Objects.requireNonNull(key, "key");
        return values.containsKey(key);
    }
}