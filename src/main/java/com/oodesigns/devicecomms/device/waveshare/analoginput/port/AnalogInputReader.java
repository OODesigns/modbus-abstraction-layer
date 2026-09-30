package com.oodesigns.devicecomms.device.waveshare.analoginput.port;

import com.oodesigns.devicecomms.device.waveshare.analoginput.reading.AnalogInputSnapshot;
import com.oodesigns.devicecomms.domain.Response;
import java.util.concurrent.CompletableFuture;

public interface AnalogInputReader {
    CompletableFuture<Response<AnalogInputSnapshot>> readChannels();
}