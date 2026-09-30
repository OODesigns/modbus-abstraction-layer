package com.oodesigns.devicecomms.domain.communication;

import com.oodesigns.devicecomms.domain.Response;
import java.util.Map;
import java.util.Objects;

public final class PointSnapshot {
    private final Map<PointAddress<?>, Object> values;

    public PointSnapshot(final Map<PointAddress<?>, ?> values) {
        this.values = Map.copyOf(Objects.requireNonNull(values, "values"));
    }

    public <T> Response<T> valueOf(final PointAddress<T> point) {
        Objects.requireNonNull(point, "point");
        if (!values.containsKey(point)) {
            return Response.failure("point was not included in this snapshot");
        }
        return Response.success(cast(values.get(point)));
    }

    @SuppressWarnings("unchecked")
    private static <T> T cast(final Object value) {
        return (T) value;
    }
}