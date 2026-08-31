package io.oodesigns.modbus;

import static org.junit.jupiter.api.Assertions.*;
import io.oodesigns.modbus.client.*; import io.oodesigns.modbus.plugin.*; import io.oodesigns.modbus.runtime.*; import io.oodesigns.modbus.value.*;
import java.nio.file.*; import java.time.Duration; import java.util.*; import java.util.concurrent.*;
import org.junit.jupiter.api.Test;

class CoreTest {
 @Test void valueObjectsEnforceTheirInclusiveBounds() {
  assertDoesNotThrow(()->{new StartAddress(0);new StartAddress(65535);new RegisterCount(1);new RegisterCount(125);new CoilCount(1);new CoilCount(2000);new TemperatureCelsius(-100);new TemperatureCelsius(200);});
  assertThrows(IllegalArgumentException.class,()->new StartAddress(-1)); assertThrows(IllegalArgumentException.class,()->new StartAddress(65536)); assertThrows(IllegalArgumentException.class,()->new RegisterCount(0)); assertThrows(IllegalArgumentException.class,()->new CoilCount(2001)); assertThrows(IllegalArgumentException.class,()->new TemperatureCelsius(201));
 }
 @Test void responseComposesAndConvertsMapperExceptionsToFailures() {
  assertEquals(4,Response.success(2).map(x->x*2).value());
  assertEquals(Status.EXCEPTION,Response.<Integer>failure("broken").map(x->x*2).status());
  assertEquals(Status.EXCEPTION,Response.success(2).flatMap(x->{throw new IllegalStateException("broken");}).status());
 }
 @Test void registryReportsUnregisteredTransport() {
  var registry=new ModbusClientRegistry(List.of());
  assertEquals(Status.EXCEPTION,registry.get(TransportType.RTU,settings()).status());
 }
 @Test void connectionManagerRetriesFailedConnections() {
  class Flaky implements ModbusClient { int attempts; public CompletableFuture<Response<Void>> connect(){return CompletableFuture.completedFuture(++attempts==1?Response.failure("offline"):Response.success(null));} public CompletableFuture<Response<Void>> disconnect(){return CompletableFuture.completedFuture(Response.success(null));} public CompletableFuture<Response<boolean[]>> readCoils(StartAddress a,CoilCount c){return null;} public CompletableFuture<Response<boolean[]>> readDiscreteInputs(StartAddress a,CoilCount c){return null;}public CompletableFuture<Response<int[]>> readHoldingRegisters(StartAddress a,RegisterCount c){return null;}public CompletableFuture<Response<int[]>> readInputRegisters(StartAddress a,RegisterCount c){return null;}public CompletableFuture<Response<Void>> writeCoil(StartAddress a,boolean v){return null;}public CompletableFuture<Response<Void>> writeRegister(StartAddress a,int v){return null;}public Response<Boolean> isConnected(){return Response.success(false);}}
  var flaky=new Flaky(); assertEquals(Status.OK,new ConnectionManager(flaky,new Retries(1)).connect().join().status()); assertEquals(2,flaky.attempts);
 }
 @Test void factoryReportsMissingPluginAndDependency() throws Exception {
  Path profile=Files.createTempFile("profile",".json"); Files.writeString(profile,"{\"name\":\"test\"}");
  var factory=new DeviceFactory(List.of(),new ConfigFactory(),new Dependencies(Map.of()));
  assertTrue(factory.createDevice(new DeviceType("unknown"),profile).details().startsWith("NOT_REGISTERED"));
  DevicePlugin plugin=new DevicePlugin(){public DeviceType deviceType(){return new DeviceType("test");}public Set<DependencyKey> requiredDependencies(){return Set.of(new DependencyKey("modbus"));}public Response<Device> create(ConfigLoader c,Dependencies d){return Response.failure("unexpected");}};
  assertTrue(new DeviceFactory(List.of(plugin),new ConfigFactory(),new Dependencies(Map.of())).createDevice(new DeviceType("test"),profile).details().startsWith("MISSING_DEPENDENCY"));
 }
 @Test void transformerCascadesTheOriginalFailure() {
  Response<Integer> upstream=Response.failure("read failed"); Response<TemperatureCelsius> result=new TemperatureTransformer().transform(upstream);
  assertEquals(Status.EXCEPTION,result.status()); assertEquals("read failed",result.details());
 }
 private static ConnectionSettings settings(){return new ConnectionSettings(new IPAddress("127.0.0.1"),new Port(502),new SerialPortName("COM1"),new BaudRate(9600),new UnitId(1),new Timeout(Duration.ofSeconds(1)),new Retries(1));}
}
