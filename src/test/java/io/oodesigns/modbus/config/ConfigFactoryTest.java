package io.oodesigns.modbus.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.oodesigns.modbus.core.Response;
import io.oodesigns.modbus.core.Status;
import io.oodesigns.modbus.value.DeviceType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Device profile configuration")
class ConfigFactoryTest {

    private static final DeviceType FAKE_SENSOR = new DeviceType("fake_temp_sensor");

    @Test
    @DisplayName("loads a JSON device profile from the configured location")
    void loadsDeviceProfile() {
        Response<ConfigLoader> response = new ConfigFactory().loadFor(FAKE_SENSOR);

        assertEquals(Status.OK, response.status(), response.details());

        ConfigLoader config = response.value().orElseThrow();
        assertEquals("127.0.0.1", config.string(new ConfigKey("host")).value().orElseThrow());
        assertEquals(502, config.integer(new ConfigKey("port")).value().orElseThrow());
        assertEquals("TCP", config.string(new ConfigKey("transport")).value().orElseThrow());
    }

    @Test
    @DisplayName("returns a failure Response when the profile does not exist")
    void missingProfileProducesFailure() {
        Response<ConfigLoader> response = new ConfigFactory().loadFor(new DeviceType("no_such_device"));

        assertEquals(Status.EXCEPTION, response.status());
        assertTrue(response.details().contains("no_such_device"));
    }

    @Test
    @DisplayName("returns a failure Response when the profile is not valid JSON")
    void malformedProfileProducesFailure() {
        Response<ConfigLoader> response = new ConfigFactory().loadFor(new DeviceType("broken_device"));

        assertEquals(Status.EXCEPTION, response.status());
    }

    @Test
    @DisplayName("returns a failure Response for an unknown or mistyped key")
    void unknownKeyProducesFailure() {
        ConfigLoader config = new ConfigFactory().loadFor(FAKE_SENSOR).value().orElseThrow();

        assertEquals(Status.EXCEPTION, config.string(new ConfigKey("nope")).status());
        assertEquals(Status.EXCEPTION, config.integer(new ConfigKey("host")).status());
    }

    @Test
    @DisplayName("keys and locations are validated on construction")
    void constructionIsValidated() {
        assertThrows(IllegalArgumentException.class, () -> new ConfigKey(" "));
        assertThrows(IllegalArgumentException.class, () -> new ConfigKey(null));
        assertThrows(IllegalArgumentException.class, () -> new ConfigLocation(""));
        assertThrows(IllegalArgumentException.class, () -> new ConfigFactory(null));
    }

    @Test
    @DisplayName("loading never throws, even for a missing device type")
    void loadingNeverThrows() {
        assertEquals(Status.EXCEPTION, new ConfigFactory().loadFor(null).status());
    }
}
