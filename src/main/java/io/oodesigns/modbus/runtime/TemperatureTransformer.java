package io.oodesigns.modbus.runtime;
import io.oodesigns.modbus.*; import io.oodesigns.modbus.value.*;
/** Converts a raw tenth-degree register to its typed Celsius measurement. */
public final class TemperatureTransformer implements ResponseTransformer<Integer,TemperatureCelsius> {
 private static final SensorType TYPE=new SensorType("temperature");
 public SensorType sensorType(){return TYPE;}
 public Response<TemperatureCelsius> transform(Response<Integer> raw){return raw.map(value->new TemperatureCelsius(value/10.0));}
}
