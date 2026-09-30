package com.oodesigns.devicecomms.domain.transformer;

import com.oodesigns.devicecomms.domain.Response;
import com.oodesigns.devicecomms.domain.value.SensorType;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;

public final class ResponseTransformerFactory {
    private final Map<SensorType, ResponseTransformer<?, ?>> transformers;

    public ResponseTransformerFactory() {
        this(ServiceLoader.load(ResponseTransformerProvider.class).stream()
                .map(ServiceLoader.Provider::get)
                .toList());
    }

    public ResponseTransformerFactory(final Collection<? extends ResponseTransformerProvider> providers) {
        Objects.requireNonNull(providers, "providers");
        final Map<SensorType, ResponseTransformer<?, ?>> indexed = new HashMap<>();
        for (final ResponseTransformerProvider provider : providers) {
            Objects.requireNonNull(provider, "provider");
            if (indexed.putIfAbsent(provider.sensorType(), provider.transformer()) != null) {
                throw new IllegalArgumentException("duplicate transformer for sensor: " + provider.sensorType().value());
            }
        }
        transformers = Map.copyOf(indexed);
    }

    public Response<ResponseTransformer<?, ?>> forSensor(final SensorType type) {
        Objects.requireNonNull(type, "type");
        final ResponseTransformer<?, ?> transformer = transformers.get(type);
        return transformer == null
                ? Response.failure("transformer is not registered for sensor: " + type.value())
                : Response.success(transformer);
    }
}