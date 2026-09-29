package com.oodesigns.modbus.domain.communication;

import com.oodesigns.modbus.domain.Response;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface CommunicationClient {
    CompletableFuture<Response<Void>> connect();

    CompletableFuture<Response<Void>> disconnect();

    Response<Boolean> isConnected();

    CompletableFuture<Response<PointSnapshot>> read(List<PointAddress<?>> points);

    <T> CompletableFuture<Response<Void>> write(PointAddress<T> point, T value);
}