package com.oodesigns.devicecomms.domain.device;

import com.oodesigns.devicecomms.domain.Response;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;

public interface Polling {
    Duration interval();

    CompletableFuture<Response<Void>> pollOnce();

    PollingRuntime pollingRuntime();

    default Response<Void> startPolling(final ScheduledExecutorService executor) {
        try {
            final PollingRuntime runtime = pollingRuntime();
            final Duration pollingInterval = interval();
            if (runtime == null || pollingInterval == null) {
                return recordStartFailure("polling runtime and interval are required");
            }
            final Response<Void> started = runtime.start(executor, pollingInterval, () -> pollAndObserve(runtime));
            return started.status() == Response.Status.OK
                    ? started
                    : recordStartFailure(started.details());
        } catch (final RuntimeException exception) {
            return recordStartFailure(message(exception));
        }
    }

    private void pollAndObserve(final PollingRuntime runtime) {
        try {
            final CompletableFuture<Response<Void>> polling = pollOnce();
            if (polling == null) {
                recordFailure(runtime, "pollOnce returned no future");
                return;
            }
            polling.whenComplete((result, error) -> {
                if (error != null || result == null || result.status() != Response.Status.OK) {
                    recordFailure(runtime, error == null
                            ? result == null ? "pollOnce returned no response" : result.details()
                            : message(error));
                }
            });
        } catch (final RuntimeException exception) {
            recordFailure(runtime, message(exception));
        }
    }

    private Response<Void> recordStartFailure(final String details) {
        final PollingRuntime runtime = pollingRuntime();
        if (runtime != null) {
            recordFailure(runtime, details);
        }
        return Response.failure(details == null || details.isBlank() ? "polling failed to start" : details);
    }

    private void recordFailure(final PollingRuntime runtime, final String details) {
        runtime.stop();
        if (this instanceof StatefulDevice stateful) {
            try {
                stateful.stateManager().updateState(Map.of("polling", false),
                        Map.of("START_FAILURE", details == null || details.isBlank() ? "polling failed" : details));
            } catch (final RuntimeException ignored) {
                runtime.stop();
            }
        }
    }

    private static String message(final Throwable throwable) {
        final String detail = throwable.getMessage();
        return detail == null || detail.isBlank() ? "polling failed" : detail;
    }

    default Response<Void> stopPolling() {
        final PollingRuntime runtime = pollingRuntime();
        return runtime == null ? Response.failure("polling runtime is required") : runtime.stop();
    }
}