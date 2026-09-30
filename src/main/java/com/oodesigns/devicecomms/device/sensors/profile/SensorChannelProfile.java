package com.oodesigns.devicecomms.device.sensors.profile;

import com.oodesigns.devicecomms.device.waveshare.analoginput.value.AnalogChannelNumber;
import java.util.Objects;

public record SensorChannelProfile(SensorIdentity identity,
                                   AnalogChannelNumber channel,
                                   SensorSignalScale scale) {
    public SensorChannelProfile {
        Objects.requireNonNull(identity, "identity");
        Objects.requireNonNull(channel, "channel");
        Objects.requireNonNull(scale, "scale");
    }
}