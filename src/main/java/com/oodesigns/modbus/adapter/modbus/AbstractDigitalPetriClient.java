package com.oodesigns.modbus.adapter.modbus;

import com.digitalpetri.modbus.client.ModbusClient;
import com.digitalpetri.modbus.pdu.ReadCoilsRequest;
import com.digitalpetri.modbus.pdu.ReadDiscreteInputsRequest;
import com.digitalpetri.modbus.pdu.ReadHoldingRegistersRequest;
import com.digitalpetri.modbus.pdu.ReadInputRegistersRequest;
import com.digitalpetri.modbus.pdu.WriteSingleCoilRequest;
import com.digitalpetri.modbus.pdu.WriteSingleRegisterRequest;
import com.oodesigns.modbus.domain.Response;
import com.oodesigns.modbus.domain.communication.CommunicationClient;
import com.oodesigns.modbus.domain.communication.PointAddress;
import com.oodesigns.modbus.domain.communication.PointSnapshot;
import com.oodesigns.modbus.domain.value.UnitId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

abstract class AbstractDigitalPetriClient implements CommunicationClient {
    private final ModbusClient client;
    private final UnitId unitId;

    AbstractDigitalPetriClient(final ModbusClient client, final UnitId unitId) {
        this.client = client;
        this.unitId = unitId;
    }

    @Override
    public CompletableFuture<Response<Void>> connect() {
        return fromStage(client.connectAsync(), ignored -> null);
    }

    @Override
    public CompletableFuture<Response<Void>> disconnect() {
        return fromStage(client.disconnectAsync(), ignored -> null);
    }

    @Override
    public Response<Boolean> isConnected() {
        try {
            return Response.success(client.isConnected());
        } catch (final RuntimeException exception) {
            return Response.failure(message(exception));
        }
    }

    @Override
    public CompletableFuture<Response<PointSnapshot>> read(final List<PointAddress<?>> points) {
        if (points == null) {
            return CompletableFuture.completedFuture(Response.failure("points are required"));
        }
        try {
            return readNext(List.copyOf(points), 0, new HashMap<>());
        } catch (final RuntimeException exception) {
            return CompletableFuture.completedFuture(Response.failure(message(exception)));
        }
    }

    @Override
    public <T> CompletableFuture<Response<Void>> write(final PointAddress<T> point, final T value) {
        if (point == null || value == null) {
            return CompletableFuture.completedFuture(Response.failure("point and value are required"));
        }
        try {
            if (point instanceof CoilPoint coil && value instanceof Boolean state) {
                return fromStage(client.writeSingleCoilAsync(unitId.value(),
                        new WriteSingleCoilRequest(coil.address().value(), state)), ignored -> null);
            }
            if (point instanceof HoldingRegisterPoint register && value instanceof Integer word
                    && word >= 0 && word <= 65535) {
                return fromStage(client.writeSingleRegisterAsync(unitId.value(),
                        new WriteSingleRegisterRequest(register.address().value(), word)), ignored -> null);
            }
            return CompletableFuture.completedFuture(Response.failure("point type or value is not writable"));
        } catch (final RuntimeException exception) {
            return CompletableFuture.completedFuture(Response.failure(message(exception)));
        }
    }

    private CompletableFuture<Response<PointSnapshot>> readNext(final List<PointAddress<?>> points,
                                                                  final int index,
                                                                  final Map<PointAddress<?>, Object> values) {
        if (index == points.size()) {
            return CompletableFuture.completedFuture(Response.success(new PointSnapshot(values)));
        }
        final PointAddress<?> point = points.get(index);
        return readPoint(point).thenCompose(result -> result.fold(
                value -> {
                    values.put(point, value);
                    return readNext(points, index + 1, values);
                },
                failure -> CompletableFuture.completedFuture(Response.failure(failure.details()))));
    }

    private CompletableFuture<Response<Object>> readPoint(final PointAddress<?> point) {
        try {
            if (point instanceof CoilPoint coil) {
                return fromStage(client.readCoilsAsync(unitId.value(),
                        new ReadCoilsRequest(coil.address().value(), 1)), response -> (Object) bit(response.coils()));
            }
            if (point instanceof DiscreteInputPoint input) {
                return fromStage(client.readDiscreteInputsAsync(unitId.value(),
                        new ReadDiscreteInputsRequest(input.address().value(), 1)),
                        response -> (Object) bit(response.inputs()));
            }
            if (point instanceof HoldingRegisterPoint register) {
                return fromStage(client.readHoldingRegistersAsync(unitId.value(),
                        new ReadHoldingRegistersRequest(register.address().value(), 1)),
                        response -> (Object) word(response.registers()));
            }
            if (point instanceof InputRegisterPoint register) {
                return fromStage(client.readInputRegistersAsync(unitId.value(),
                        new ReadInputRegistersRequest(register.address().value(), 1)),
                        response -> (Object) word(response.registers()));
            }
            return CompletableFuture.completedFuture(Response.failure("unsupported Modbus point type"));
        } catch (final RuntimeException exception) {
            return CompletableFuture.completedFuture(Response.failure(message(exception)));
        }
    }

    private static boolean bit(final byte[] bytes) {
        if (bytes.length == 0) {
            throw new IllegalArgumentException("Modbus returned an empty bit response");
        }
        return (bytes[0] & 1) != 0;
    }

    private static int word(final byte[] bytes) {
        if (bytes.length < 2) {
            throw new IllegalArgumentException("Modbus returned an incomplete register response");
        }
        return Byte.toUnsignedInt(bytes[0]) << 8 | Byte.toUnsignedInt(bytes[1]);
    }

    private static <T, V> CompletableFuture<Response<V>> fromStage(final CompletionStage<T> stage,
                                                                    final Function<T, V> mapper) {
        if (stage == null) {
            return CompletableFuture.completedFuture(Response.failure("Modbus client returned no operation"));
        }
        return stage.handle((value, error) -> {
            if (error != null) {
                return Response.<V>failure(message(error));
            }
            try {
                return Response.<V>success(mapper.apply(value));
            } catch (final RuntimeException exception) {
                return Response.<V>failure(message(exception));
            }
        }).toCompletableFuture();
    }

    private static String message(final Throwable throwable) {
        final String detail = throwable.getMessage();
        return detail == null || detail.isBlank() ? "Modbus operation failed" : detail;
    }
}