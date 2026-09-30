package com.oodesigns.devicecomms.domain.device;

import com.oodesigns.devicecomms.domain.Response;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public final class StateManager {
    private final AtomicReference<Snapshot> snapshot = new AtomicReference<>(new Snapshot(Map.of(), Map.of()));

    public Response<Void> updateState(final Map<String, Boolean> operational,
                                      final Map<String, String> triggeredRules) {
        if (operational == null || triggeredRules == null) {
            return Response.failure("state maps must not be null");
        }
        try {
            snapshot.set(new Snapshot(operational, triggeredRules));
            return Response.success();
        } catch (final RuntimeException exception) {
            return Response.failure("state update failed: " + exception.getMessage());
        }
    }

    public Snapshot snapshot() {
        return snapshot.get();
    }

    public record Snapshot(Map<String, Boolean> operational, Map<String, String> triggeredRules) {
        public Snapshot {
            operational = Map.copyOf(operational);
            triggeredRules = Map.copyOf(triggeredRules);
        }
    }
}