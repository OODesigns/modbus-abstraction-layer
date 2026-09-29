package com.oodesigns.modbus.domain.communication;

import com.oodesigns.modbus.domain.Response;
import com.oodesigns.modbus.domain.value.Retries;
import com.oodesigns.modbus.domain.value.Timeout;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public final class ConnectionManager implements CommunicationClient {
    private final CommunicationClient delegate;
    private final Retries retries;
    private final Timeout timeout;

    public ConnectionManager(final CommunicationClient delegate, final Retries retries, final Timeout timeout) {
        this.delegate = Objects.requireNonNull(delegate, "delegate");
        this.retries = Objects.requireNonNull(retries, "retries");
        this.timeout = Objects.requireNonNull(timeout, "timeout");
    }

    @Override
    public CompletableFuture<Response<Void>> connect() {
        return retry(() -> invoke(delegate::connect), 0, false);
    }

    @Override
    public CompletableFuture<Response<Void>> disconnect() {
        return invoke(delegate::disconnect);
    }

    @Override
    public Response<Boolean> isConnected() {
        try {
            return Objects.requireNonNull(delegate.isConnected(), "delegate result");
        } catch (final RuntimeException exception) {
            return Response.failure(message("connection status failed", exception));
        }
    }

    @Override
    public CompletableFuture<Response<PointSnapshot>> read(final List<PointAddress<?>> points) {
        if (points == null) {
            return CompletableFuture.completedFuture(Response.failure("points are required"));
        }
        return retry(() -> invoke(() -> delegate.read(List.copyOf(points))), 0, true);
    }

    @Override
    public <T> CompletableFuture<Response<Void>> write(final PointAddress<T> point, final T value) {
        if (point == null || value == null) {
            return CompletableFuture.completedFuture(Response.failure("point and value are required"));
        }
        return retry(() -> invoke(() -> delegate.write(point, value)), 0, true);
    }

    private <T> CompletableFuture<Response<T>> retry(final Supplier<CompletableFuture<Response<T>>> operation,
                                                      final int retryNumber,
                                                      final boolean reconnectBeforeRetry) {
        return safely(operation).thenCompose(response -> {
            if (response.status() == Response.Status.OK || retryNumber >= retries.value()) {
                return CompletableFuture.completedFuture(response);
            }
            final CompletableFuture<Response<Void>> reconnect = reconnectBeforeRetry
                    ? invoke(delegate::connect)
                    : CompletableFuture.completedFuture(Response.success());
            return reconnect.thenCompose(ignored -> retry(operation, retryNumber + 1, reconnectBeforeRetry));
        });
    }

    private <T> CompletableFuture<Response<T>> safely(final Supplier<CompletableFuture<Response<T>>> operation) {
        try {
            return Objects.requireNonNull(operation.get(), "delegate future")
                    .copy()
                    .orTimeout(timeout.value().toNanos(), TimeUnit.NANOSECONDS)
                    .handle((response, error) -> {
                        if (error != null) {
                            return Response.failure(message("communication operation failed", unwrap(error)));
                        }
                        return response == null
                                ? Response.failure("communication delegate returned no response")
                                : response;
                    });
        } catch (final RuntimeException exception) {
            return CompletableFuture.completedFuture(Response.failure(message("communication operation failed", exception)));
        }
    }

    private <T> CompletableFuture<Response<T>> invoke(final Supplier<CompletableFuture<Response<T>>> operation) {
        return safely(operation);
    }

    private static Throwable unwrap(final Throwable throwable) {
        return throwable instanceof CompletionException && throwable.getCause() != null
                ? throwable.getCause()
                : throwable;
    }

    private static String message(final String prefix, final Throwable exception) {
        final String detail = exception.getMessage();
        return detail == null || detail.isBlank() ? prefix : prefix + ": " + detail;
    }
}