package com.oodesigns.modbus.domain.communication;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.oodesigns.modbus.domain.Response;
import java.util.Map;
import org.junit.jupiter.api.Test;

class PointSnapshotTest {
    private record IntegerPoint(String name) implements PointAddress<Integer> {
    }

    @Test
    void returnsTypedValueAndFailureForUnrequestedPoint() {
        final IntegerPoint requested = new IntegerPoint("temperature");
        final PointSnapshot snapshot = new PointSnapshot(Map.of(requested, 23));

        assertEquals(23, snapshot.valueOf(requested).value());
        assertEquals(Response.Status.EXCEPTION, snapshot.valueOf(new IntegerPoint("other")).status());
    }
}