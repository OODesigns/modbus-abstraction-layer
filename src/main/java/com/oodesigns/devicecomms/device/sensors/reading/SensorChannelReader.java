package com.oodesigns.devicecomms.device.sensors.reading;

import com.oodesigns.devicecomms.device.waveshare.analoginput.reading.AnalogChannelReading;
import com.oodesigns.devicecomms.device.waveshare.analoginput.port.AnalogInputReader;
import com.oodesigns.devicecomms.device.sensors.profile.SensorChannelProfile;
import com.oodesigns.devicecomms.domain.Response;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public final class SensorChannelReader {
    private SensorChannelReader() {
    }

    public static CompletableFuture<Response<ScaledSensorMeasurement>> read(
            final AnalogInputReader inputReader, final SensorChannelProfile channelProfile) {
        try {
            return inputReader.readChannels().handle((result, error) -> {
                if (error != null) {
                    final Throwable cause = error instanceof CompletionException && error.getCause() != null
                            ? error.getCause() : error;
                    if (cause instanceof Error fatalError) {
                        throw fatalError;
                    }
                    return Response.<AnalogChannelReading>failure(failureDetails(cause));
                }
                if (result == null || result.status() != Response.Status.OK) {
                    return Response.<AnalogChannelReading>failure(result == null
                            ? "analog input result is missing" : result.details());
                }
                return result.value().channels().stream()
                        .filter(reading -> reading.channel().equals(channelProfile.channel()))
                        .findFirst()
                        .map(Response::<AnalogChannelReading>success)
                        .orElseGet(() -> Response.failure("configured sensor channel is missing"));
            }).thenApply(channel -> channel.flatMap(channelProfile.scale()::convert)).toCompletableFuture();
        } catch (final RuntimeException exception) {
            return CompletableFuture.completedFuture(Response.failure(failureDetails(exception)));
        }
    }

    private static String failureDetails(final Throwable exception) {
        final String message = exception.getMessage();
        return message == null || message.isBlank() ? "sensor reading failed" : message;
    }
}