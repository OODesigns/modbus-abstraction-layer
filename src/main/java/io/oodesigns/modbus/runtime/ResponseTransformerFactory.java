package io.oodesigns.modbus.runtime;
import io.oodesigns.modbus.Response; import io.oodesigns.modbus.value.SensorType; import java.util.*;
public final class ResponseTransformerFactory {
 private final Map<SensorType,ResponseTransformer<?,?>> transformers;
 @SuppressWarnings({"rawtypes", "unchecked"})
 public ResponseTransformerFactory(){this((Iterable) ServiceLoader.load(ResponseTransformer.class));}
 public ResponseTransformerFactory(Iterable<ResponseTransformer<?,?>> transformers){Map<SensorType,ResponseTransformer<?,?>> found=new HashMap<>();transformers.forEach(t->found.put(t.sensorType(),t));this.transformers=Map.copyOf(found);}
 public Response<ResponseTransformer<?,?>> forSensor(SensorType type){if(type==null)return Response.failure("sensor type is required");var result=transformers.get(type);return result==null?Response.failure("NOT_REGISTERED: "+type.value()):Response.success(result);}
}
