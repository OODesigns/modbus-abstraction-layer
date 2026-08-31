package io.oodesigns.modbus.runtime;
import io.oodesigns.modbus.Response; import io.oodesigns.modbus.value.SensorType;
public interface ResponseTransformer<R,V> { SensorType sensorType(); Response<V> transform(Response<R> raw); }
