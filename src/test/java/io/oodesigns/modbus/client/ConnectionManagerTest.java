package io.oodesigns.modbus.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.oodesigns.modbus.core.Response;
import io.oodesigns.modbus.core.Status;
import io.oodesigns.modbus.fake.FakeModbusClient;
import io.oodesigns.modbus.value.RegisterCount;
import io.oodesigns.modbus.value.RegisterValues;
import io.oodesigns.modbus.value.Retries;
import io.oodesigns.modbus.value.StartAddress;
import io.oodesigns.modbus.value.Timeout;
import java.time.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ConnectionManager")
class ConnectionManagerTest {

    private static final Timeout TIMEOUT = new Timeout(Duration.ofSeconds(2));
    private static final StartAddress START = new StartAddress(0);
    private static final RegisterCount COUNT = new RegisterCount(2);

    @Test
    @DisplayName("retries a failing connect up to the configured retry count")
    void retriesConnect() {
        FakeModbusClient delegate = new FakeModbusClient().failConnects(2);
        ConnectionManager manager = new ConnectionManager(delegate, new Retries(2), TIMEOUT);

        Response<Void> response = manager.connect().join();

        assertEquals(Status.OK, response.status());
        assertEquals(3, delegate.connectAttempts());
        assertTrue(manager.isConnected());
    }

    @Test
    @DisplayName("gives up with a failure Response once the retries are exhausted")
    void givesUpAfterRetries() {
        FakeModbusClient delegate = new FakeModbusClient().failConnects(5);
        ConnectionManager manager = new ConnectionManager(delegate, new Retries(1), TIMEOUT);

        Response<Void> response = manager.connect().join();

        assertEquals(Status.EXCEPTION, response.status());
        assertTrue(response.details().contains("connect refused"));
        assertEquals(2, delegate.connectAttempts());
    }

    @Test
    @DisplayName("retries a failing read and returns the eventual success")
    void retriesRead() {
        FakeModbusClient delegate = new FakeModbusClient().failReads(1).withRegisters(10, 20);
        ConnectionManager manager = new ConnectionManager(delegate, new Retries(2), TIMEOUT);
        manager.connect().join();

        Response<RegisterValues> response = manager.readHoldingRegisters(START, COUNT).join();

        assertEquals(Status.OK, response.status());
        assertEquals(10, response.value().orElseThrow().at(0).orElseThrow().value());
        assertEquals(2, delegate.readAttempts());
    }

    @Test
    @DisplayName("reconnects transparently when the delegate has lost its connection")
    void reconnectsBeforeReading() {
        FakeModbusClient delegate = new FakeModbusClient().withRegisters(7);
        ConnectionManager manager = new ConnectionManager(delegate, new Retries(1), TIMEOUT);
        manager.connect().join();
        delegate.dropConnection();

        Response<RegisterValues> response = manager.readHoldingRegisters(START, COUNT).join();

        assertEquals(Status.OK, response.status());
        assertEquals(2, delegate.connectAttempts());
    }

    @Test
    @DisplayName("a delegate that never answers completes normally with a timeout failure")
    void honoursTimeout() {
        ConnectionManager manager = new ConnectionManager(
                new FakeModbusClient().neverAnswers(), new Retries(0), new Timeout(Duration.ofMillis(50)));

        Response<Void> response = manager.connect().join();

        assertEquals(Status.EXCEPTION, response.status());
        assertTrue(response.details().toLowerCase().contains("timed out"));
    }

    @Test
    @DisplayName("disconnect delegates and never throws")
    void disconnectDelegates() {
        FakeModbusClient delegate = new FakeModbusClient();
        ConnectionManager manager = new ConnectionManager(delegate, new Retries(0), TIMEOUT);

        assertEquals(Status.OK, manager.disconnect().status());
        assertEquals(1, delegate.disconnects());
    }

    @Test
    @DisplayName("its collaborators are construction preconditions")
    void collaboratorsAreRequired() {
        assertThrows(IllegalArgumentException.class,
                () -> new ConnectionManager(null, new Retries(0), TIMEOUT));
        assertThrows(IllegalArgumentException.class,
                () -> new ConnectionManager(new FakeModbusClient(), null, TIMEOUT));
        assertThrows(IllegalArgumentException.class,
                () -> new ConnectionManager(new FakeModbusClient(), new Retries(0), null));
    }
}
