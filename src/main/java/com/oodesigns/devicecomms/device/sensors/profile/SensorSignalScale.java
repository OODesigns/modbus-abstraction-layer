package com.oodesigns.devicecomms.device.sensors.profile;

import com.oodesigns.devicecomms.device.waveshare.analoginput.reading.AnalogChannelReading;
import com.oodesigns.devicecomms.domain.Response;
import com.oodesigns.devicecomms.device.sensors.reading.ScaledSensorMeasurement;
import java.util.Objects;

public final class SensorSignalScale {
    private final ElectricalSignalMode electricalMode;
    private final SensorMeasurementRange measurementRange;

    public SensorSignalScale(final ElectricalSignalMode electricalMode,
                             final SensorMeasurementRange measurementRange) {
        this.electricalMode = Objects.requireNonNull(electricalMode, "electricalMode");
        this.measurementRange = Objects.requireNonNull(measurementRange, "measurementRange");
    }

    public Response<ScaledSensorMeasurement> convert(final AnalogChannelReading reading) {
        if (reading == null) {
            return Response.failure("analog channel reading is required");
        }
        return electricalMode.read(reading).map(raw -> {
            final RawSignalRange rawRange = electricalMode.rawRange();
            final double fraction = (double) (raw.value() - rawRange.minimum())
                    / (rawRange.maximum() - rawRange.minimum());
            return new ScaledSensorMeasurement(measurementRange.minimum()
                    + fraction * (measurementRange.maximum() - measurementRange.minimum()));
        });
    }

    public SensorMeasurementRange measurementRange() {
        return measurementRange;
    }
}