package com.oodesigns.devicecomms.testkit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.oodesigns.devicecomms.domain.Response;
import com.oodesigns.devicecomms.domain.communication.PointAddress;
import java.util.List;
import org.junit.jupiter.api.Test;

class InMemoryCommunicationClientTest {
    private record BooleanPoint(String name) implements PointAddress<Boolean> {
    }

    @Test
    void connectsWritesAndReadsTypedPoints() {
        final InMemoryCommunicationClient client = new InMemoryCommunicationClient();
        final BooleanPoint point = new BooleanPoint("relay");
        client.seed(point, false);

        assertEquals(Response.Status.OK, client.connect().join().status());
        assertEquals(Response.Status.OK, client.write(point, true).join().status());
        assertEquals(true, client.read(List.of(point)).join().value().valueOf(point).value());
        assertEquals(Response.Status.OK, client.disconnect().join().status());
        assertEquals(false, client.isConnected().value());
    }
}