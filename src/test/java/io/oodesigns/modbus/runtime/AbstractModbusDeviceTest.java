package io.oodesigns.modbus.runtime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.oodesigns.modbus.client.ModbusClient;
import io.oodesigns.modbus.core.Response;
import io.oodesigns.modbus.core.Status;
import io.oodesigns.modbus.device.DeviceState;
import io.oodesigns.modbus.device.RunState;
import io.oodesigns.modbus.device.TriggeredRule;
import io.oodesigns.modbus.fake.FakeModbusClient;
import io.oodesigns.modbus.value.PollInterval;
import io.oodesigns.modbus.value.RegisterCount;
import io.oodesigns.modbus.value.StartAddress;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("AbstractModbusDevice")
class AbstractModbusDeviceTest {

    private static final PollInterval FAST = new PollInterval(Duration.ofMillis(20));

    /** Device under test: reads a single holding register and publishes it as operational state. */
    private static final class TestDevice extends AbstractModbusDevice {

        private final AtomicInteger reads = new AtomicInteger();

        private TestDevice(ModbusClient client, StateManager stateManager, PollInterval interval) {
            super(client, stateManager, interval);
        }

        @Override
        protected CompletableFuture<Response<DeviceState>> readData() {
            reads.incrementAndGet();
            return client().readHoldingRegisters(new StartAddress(0), new RegisterCount(1))
                    .thenApply(response -> response.map(registers -> DeviceState.operational(
                            Map.of("raw", registers.at(0).orElseThrow().value()))));
        }
    }

    private static boolean awaitTrue(java.util.function.BooleanSupplier condition) {
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
        while (System.nanoTime() < deadline) {
            if (condition.getAsBoolean()) {
                return true;
            }
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return condition.getAsBoolean();
    }

    @Test
    @DisplayName("connects, marks itself running and polls the device")
    void connectsAndPolls() {
        StateManager stateManager = new StateManager();
        TestDevice device = new TestDevice(new FakeModbusClient().withRegisters(215), stateManager, FAST);

        assertEquals(Status.OK, device.open().join().status());
        assertEquals(RunState.RUNNING, stateManager.runState());
        assertTrue(awaitTrue(() -> stateManager.current().operational().containsKey("raw")));
        assertEquals(215, stateManager.current().operational().get("raw"));

        device.close();
    }

    @Test
    @DisplayName("a failed connect marks the device stopped and records START_FAILURE")
    void failedConnectRecordsTriggeredRule() {
        StateManager stateManager = new StateManager();
        TestDevice device = new TestDevice(new FakeModbusClient().failConnects(99), stateManager, FAST);

        Response<Void> response = device.open().join();

        assertEquals(Status.EXCEPTION, response.status());
        assertEquals(RunState.STOPPED, stateManager.runState());
        assertTrue(stateManager.current().triggeredRules().containsKey(TriggeredRule.START_FAILURE));
        assertEquals(0, device.reads.get());
    }

    @Test
    @DisplayName("a failed read is recorded as a triggered rule and does not stop the poll loop")
    void failedReadIsRecorded() {
        StateManager stateManager = new StateManager();
        TestDevice device = new TestDevice(new FakeModbusClient().failReads(1).withRegisters(1), stateManager, FAST);
        device.open().join();

        assertTrue(awaitTrue(() -> stateManager.current().triggeredRules().containsKey(TriggeredRule.READ_FAILURE)));
        assertTrue(awaitTrue(() -> stateManager.current().operational().containsKey("raw")));

        device.close();
    }

    @Test
    @DisplayName("read exposes the latest reading as a Response and never throws")
    void readReturnsResponse() {
        TestDevice device = new TestDevice(new FakeModbusClient().withRegisters(42), new StateManager(), FAST);
        device.open().join();

        Response<DeviceState> response = device.read().join();

        assertEquals(Status.OK, response.status());
        assertEquals(42, response.value().orElseThrow().operational().get("raw"));

        device.close();
    }

    @Test
    @DisplayName("close stops the poll loop and disconnects")
    void closeStopsPolling() {
        FakeModbusClient client = new FakeModbusClient().withRegisters(1);
        StateManager stateManager = new StateManager();
        TestDevice device = new TestDevice(client, stateManager, FAST);
        device.open().join();

        assertEquals(Status.OK, device.close().status());
        assertEquals(RunState.STOPPED, stateManager.runState());
        assertEquals(1, client.disconnects());

        int readsAfterClose = device.reads.get();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        assertTrue(device.reads.get() <= readsAfterClose + 1);
    }

    @Test
    @DisplayName("its collaborators are construction preconditions")
    void collaboratorsAreRequired() {
        assertThrows(IllegalArgumentException.class,
                () -> new TestDevice(null, new StateManager(), FAST));
        assertThrows(IllegalArgumentException.class,
                () -> new TestDevice(new FakeModbusClient(), null, FAST));
        assertThrows(IllegalArgumentException.class,
                () -> new TestDevice(new FakeModbusClient(), new StateManager(), null));
    }
}
