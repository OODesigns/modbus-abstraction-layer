package com.oodesigns.devicecomms.domain.device;

import com.oodesigns.devicecomms.domain.Response;
import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public final class PollingRuntime {
    private final AtomicReference<ScheduledFuture<?>> scheduledTask = new AtomicReference<>();

    public Response<Void> start(final ScheduledExecutorService executor,
                                final Duration interval,
                                final Runnable poll) {
        if (executor == null || interval == null || poll == null) {
            return Response.failure("executor, interval and poll action are required");
        }
        if (interval.isZero() || interval.isNegative()) {
            return Response.failure("polling interval must be positive");
        }
        if (isRunning()) {
            return Response.success();
        }
        try {
            final ScheduledFuture<?> task = executor.scheduleAtFixedRate(
                    poll, 0, interval.toNanos(), TimeUnit.NANOSECONDS);
            if (!scheduledTask.compareAndSet(null, task)) {
                task.cancel(false);
            }
            return Response.success();
        } catch (final RuntimeException exception) {
            return Response.failure("polling could not start: " + exception.getMessage());
        }
    }

    public Response<Void> stop() {
        final ScheduledFuture<?> task = scheduledTask.getAndSet(null);
        if (task != null) {
            task.cancel(false);
        }
        return Response.success();
    }

    public boolean isRunning() {
        final ScheduledFuture<?> task = scheduledTask.get();
        return task != null && !task.isCancelled() && !task.isDone();
    }
}