package com.oodesigns.modbus.domain.device;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.oodesigns.modbus.domain.Response;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.Test;

class PollingTest {
    @Test
    void recordsStartFailureWhenSchedulerRejectsPolling() {
        final PollingFixture fixture = new PollingFixture();
        final var executor = Executors.newSingleThreadScheduledExecutor();
        executor.shutdown();

        final Response<Void> response = fixture.startPolling(executor);

        assertEquals(Response.Status.EXCEPTION, response.status());
        assertEquals(false, fixture.stateManager().snapshot().operational().get("polling"));
        org.junit.jupiter.api.Assertions.assertFalse(
            fixture.stateManager().snapshot().triggeredRules().get("START_FAILURE").isBlank());
    }

    private static final class PollingFixture implements Polling, StatefulDevice {
        private final PollingRuntime runtime = new PollingRuntime();
        private final StateManager stateManager = new StateManager();

        @Override
        public Duration interval() {
            return Duration.ofDays(1);
        }

        @Override
        public CompletableFuture<Response<Void>> pollOnce() {
            return CompletableFuture.completedFuture(Response.success());
        }

        @Override
        public PollingRuntime pollingRuntime() {
            return runtime;
        }

        @Override
        public StateManager stateManager() {
            return stateManager;
        }
    }
}