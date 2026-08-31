package io.oodesigns.modbus.runtime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.oodesigns.modbus.core.Status;
import io.oodesigns.modbus.device.DeviceState;
import io.oodesigns.modbus.device.RunState;
import io.oodesigns.modbus.device.TriggeredRule;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("StateManager")
class StateManagerTest {

    @Test
    @DisplayName("starts stopped with an empty state")
    void startsStopped() {
        StateManager manager = new StateManager();

        assertEquals(RunState.STOPPED, manager.runState());
        assertTrue(manager.current().operational().isEmpty());
        assertTrue(manager.current().triggeredRules().isEmpty());
    }

    @Test
    @DisplayName("records the latest operational values and triggered rules")
    void recordsState() {
        StateManager manager = new StateManager();

        assertEquals(Status.OK, manager.updateState(
                DeviceState.of(Map.of("temperature", 21.5),
                        Map.of(TriggeredRule.START_FAILURE, "connect refused"))).status());

        assertEquals(21.5, manager.current().operational().get("temperature"));
        assertEquals("connect refused", manager.current().triggeredRules().get(TriggeredRule.START_FAILURE));
    }

    @Test
    @DisplayName("updating with no state is a failure Response, never an exception")
    void nullStateProducesFailure() {
        assertEquals(Status.EXCEPTION, new StateManager().updateState(null).status());
    }

    @Test
    @DisplayName("marks running and stopped without ever throwing")
    void marksRunState() {
        StateManager manager = new StateManager();

        assertEquals(Status.OK, manager.markRunning().status());
        assertEquals(RunState.RUNNING, manager.runState());

        assertEquals(Status.OK, manager.markStopped(TriggeredRule.START_FAILURE, "connect refused").status());
        assertEquals(RunState.STOPPED, manager.runState());
        assertEquals("connect refused", manager.current().triggeredRules().get(TriggeredRule.START_FAILURE));
    }

    @Test
    @DisplayName("device state is immutable and validated on construction")
    void deviceStateIsImmutableAndValidated() {
        Map<String, Object> operational = new java.util.HashMap<>(Map.of("temperature", 21.5));
        DeviceState state = DeviceState.operational(operational);

        operational.put("temperature", 99.9);

        assertEquals(21.5, state.operational().get("temperature"));
        assertThrows(UnsupportedOperationException.class, () -> state.operational().put("x", 1));
        assertThrows(IllegalArgumentException.class, () -> DeviceState.of(null, Map.of()));
        assertThrows(IllegalArgumentException.class, () -> DeviceState.of(Map.of(), null));
    }
}
