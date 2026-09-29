package com.oodesigns.modbus.testkit;

import com.oodesigns.modbus.domain.Response;
import com.oodesigns.modbus.domain.communication.CommunicationClient;
import com.oodesigns.modbus.domain.communication.PointAddress;
import com.oodesigns.modbus.domain.communication.PointSnapshot;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public final class InMemoryCommunicationClient implements CommunicationClient {
    private final AtomicBoolean connected = new AtomicBoolean();
    private final Map<PointAddress<?>, Object> values = new ConcurrentHashMap<>();

    @Override
    public CompletableFuture<Response<Void>> connect() {
        connected.set(true);
        return CompletableFuture.completedFuture(Response.success());
    }

    @Override
    public CompletableFuture<Response<Void>> disconnect() {
        connected.set(false);
        return CompletableFuture.completedFuture(Response.success());
    }

    @Override
    public Response<Boolean> isConnected() {
        return Response.success(connected.get());
    }

    @Override
    public CompletableFuture<Response<PointSnapshot>> read(final List<PointAddress<?>> points) {
        if (!connected.get()) {
            return CompletableFuture.completedFuture(Response.failure("client is disconnected"));
        }
        if (points == null) {
            return CompletableFuture.completedFuture(Response.failure("points are required"));
        }
        final Map<PointAddress<?>, Object> selected = new HashMap<>();
        for (final PointAddress<?> point : points) {
            if (!values.containsKey(point)) {
                return CompletableFuture.completedFuture(Response.failure("point has no in-memory value"));
            }
            selected.put(point, values.get(point));
        }
        return CompletableFuture.completedFuture(Response.success(new PointSnapshot(selected)));
    }

    @Override
    public <T> CompletableFuture<Response<Void>> write(final PointAddress<T> point, final T value) {
        if (!connected.get()) {
            return CompletableFuture.completedFuture(Response.failure("client is disconnected"));
        }
        if (point == null || value == null) {
            return CompletableFuture.completedFuture(Response.failure("point and value are required"));
        }
        values.put(point, value);
        return CompletableFuture.completedFuture(Response.success());
    }

    public <T> void seed(final PointAddress<T> point, final T value) {
        values.put(point, value);
    }
}