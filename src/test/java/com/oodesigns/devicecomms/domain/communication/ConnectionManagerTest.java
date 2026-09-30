package com.oodesigns.devicecomms.domain.communication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.oodesigns.devicecomms.domain.Response;
import com.oodesigns.devicecomms.domain.value.Retries;
import com.oodesigns.devicecomms.domain.value.Timeout;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class ConnectionManagerTest {
    private record IntegerPoint() implements PointAddress<Integer> {
    }

    @Test
    void reconnectsAndRetriesFailedReadsAsResponses() {
        final IntegerPoint point = new IntegerPoint();
        final AtomicInteger attempts = new AtomicInteger();
        final AtomicInteger connections = new AtomicInteger();
        final CommunicationClient delegate = new StubClient() {
            @Override
            public CompletableFuture<Response<Void>> connect() {
                connections.incrementAndGet();
                return CompletableFuture.completedFuture(Response.success());
            }

            @Override
            public CompletableFuture<Response<PointSnapshot>> read(final List<PointAddress<?>> points) {
                return attempts.getAndIncrement() == 0
                        ? CompletableFuture.completedFuture(Response.failure("temporary failure"))
                        : CompletableFuture.completedFuture(Response.success(new PointSnapshot(Map.of(point, 17))));
            }
        };
        final ConnectionManager manager = new ConnectionManager(delegate, new Retries(1),
                new Timeout(Duration.ofSeconds(1)));

        final Response<PointSnapshot> response = manager.read(List.of(point)).join();

        assertEquals(Response.Status.OK, response.status());
        assertEquals(17, response.value().valueOf(point).value());
        assertEquals(2, attempts.get());
        assertEquals(1, connections.get());
    }

    @Test
    void convertsExceptionalDelegateCompletionToFailureResponse() {
        final CommunicationClient delegate = new StubClient() {
            @Override
            public CompletableFuture<Response<Void>> connect() {
                return CompletableFuture.failedFuture(new IllegalStateException("offline"));
            }
        };
        final ConnectionManager manager = new ConnectionManager(delegate, new Retries(0),
                new Timeout(Duration.ofSeconds(1)));

        final Response<Void> response = manager.connect().join();

        assertEquals(Response.Status.EXCEPTION, response.status());
        assertFalse(manager.isConnected().value());
    }

    private abstract static class StubClient implements CommunicationClient {
        @Override
        public CompletableFuture<Response<Void>> connect() {
            return CompletableFuture.completedFuture(Response.success());
        }

        @Override
        public CompletableFuture<Response<Void>> disconnect() {
            return CompletableFuture.completedFuture(Response.success());
        }

        @Override
        public Response<Boolean> isConnected() {
            return Response.success(false);
        }

        @Override
        public CompletableFuture<Response<PointSnapshot>> read(final List<PointAddress<?>> points) {
            return CompletableFuture.completedFuture(Response.failure("unused"));
        }

        @Override
        public <T> CompletableFuture<Response<Void>> write(final PointAddress<T> point, final T value) {
            return CompletableFuture.completedFuture(Response.success());
        }
    }
}