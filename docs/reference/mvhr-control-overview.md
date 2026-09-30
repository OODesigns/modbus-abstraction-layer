# MVHR Control Overview

## What the design shows

The Blauberg S21 MVHR is a registered device plugin above the protocol adapter.
Application code uses semantic commands and queries; it does not send raw
Modbus function codes or addresses. The plugin resolves TCP or RTU from the
profile, and control operations use the shared `CommunicationClient`.

```text
Application
  -> Device.execute(command) or Device.read(query)
  -> BlaubergMVHR maps the intent to named points
  -> CommunicationClient reads/writes typed points
  -> CommunicationClientRegistry selects Modbus TCP or RTU
  -> Blauberg S21 controller
```

The transport is configured once when the device is created. TCP uses the
`modbus-tcp` transport and a network endpoint. Serial uses `modbus-rtu` and a
serial endpoint. Both paths use the same MVHR commands and state model. The
Modbus unit ID must match the controller.

## S21 Control Map

These examples come from `docs/reference/modbus/blauberg-mobus-table.pdf` and
are specific to the listed Blauberg S21 controller. Addresses are zero-based
Modbus PDU addresses. Use the Blauberg table's `Address` values directly; do
not subtract one again. The table itself starts at address 0, consistent with
the Modbus PDU addressing convention.

| User intent | S21 point | Meaning |
|---|---|---|
| Controller power control | Coil 0, `CL_POWER` | Separate documented Boolean power control |
| Turn unit on/off | Coil 1, `CL_TIMER` | Boolean `1` is on, `0` is off; the table labels this Unit On/Off |
| Enable weekly timer | Coil 2, `CL_WEEK` | Main timer control; not yet exposed as a command |
| Read weekly/boost mode flags | Coils 3 and 4, `CL_Boost_MODE` and `CL_FPLC_MODE` | Read-only mode indicators |
| Inspect ventilation mode | Holding register 0, `HR_VENTILATION_MODE` | Read-only: 0 = 0-100% mode, 1 = constant flow, 2 = constant pressure |
| Inspect maximum preset speed | Holding register 1, `HR_MaxSPEED_MODE` | Read-only limit on the available speed presets |
| Select preset speed | Holding register 2, `HR_SPEED_MODE` | Values `1` through `5` select preset speeds |
| Set a manual fan speed | Holding register 2, then 17 | Set `HR_SPEED_MODE` to `255`, then write `HR_ManualSPEED` as `0` through `100` percent |
| Tune preset fan speeds | Holding registers 5-16 | Six modes (standby and speeds 1-5), each with supply and extract percentages; not yet exposed |
| Tune boost/fireplace fan speeds | Holding registers 19-22 | Two modes, each with separate supply and extract percentages; not yet exposed |
| Configure sensor control | Coils 5-12 | Internal/external RH, CO2, PM2.5 and VOC sensor-control flags |
| Configure other control inputs | Coils 13-16 | `CL_BoostSWITCH_CTRL`, `CL_FplcSWITCH_CTRL`, `CL_FireALARM_CTRL`, `CL_10V_SENSOR_CTRL`; not yet exposed |
| Reset maintenance indicators | Coils 17-18 | Reset filter timer or alarm; not yet exposed |
| Read discrete status and alarms | Discrete inputs 0-18 and 19 onward | Sensor, heater, filter/pressure status; current snapshot includes filter condition only |
| Read temperatures | Input registers 0-5 and 8 | Current snapshot reads selected, supply-out, and outdoor temperatures only |
| Read air quality | Input registers 10-17 | Current snapshot reads internal RH, CO2, PM2.5 and VOC only |
| Read live airflow/pressure | Input registers 19-22 | Supply/extract airflow and duct pressure |
| Read supply/extract fan RPM | Input registers 23 and 24 | `IR_SuRPM` and `IR_ExRPM`, read-only |
| Read filter condition/countdown | Input registers 27 and 31 | `IR_StateFILTER` is read; filter countdown is not yet exposed |
| Read active weekly speed | Input register 32 | `IR_CurWeekSpeed`, read-only |
| Read current weekly temperature target | Input register 33 | `IR_CurWeekSetTemp`, read-only; `0` means ventilation only, otherwise the weekly schedule target |
| Read calculated supply-air target | Input register 48 | `IR_SuAirOutSetTemp`, read-only; raw value `250` represents `25.0 deg C` |
| Change a schedule temperature | Paired weekly schedule holding register | Monday period 1 uses address 126: speed is the high byte, temperature the low byte; update preserves the speed |

This overview lists key points, not every address in the S21 table. Any
unlisted addresses should be checked in the full manufacturer table; they are
not necessarily reserved or unsupported.

The S21 table does not show one general-purpose writable live temperature
setpoint. Its temperature target is controlled by the weekly schedule; the live
values above are status/calculated values. Each schedule holding word pairs a
speed byte and temperature byte; the adjacent word pairs end hour and end
minute. The implemented temperature update reads the existing word, preserves
the speed byte, and writes the new validated temperature. `CL_POWER` and
`CL_TIMER` are distinct documented coils and have separate commands.

## Typed Values

MVHR commands and snapshots should use self-validating domain value objects,
not raw integers or generic numbers. Decode and scale the Modbus value first,
then construct the domain type; invalid values fail before a command reaches the
communication client.

Methods accept command/query objects or validated value objects. Primitive,
string, and enum inputs are permitted at command/value-object constructors,
where they are immediately validated and wrapped; business methods do not take
bare enums or raw measurement numbers.

Constructors are the only place expected invalid construction may throw.
Every other method returns a non-null typed result, including `Response<Void>`
for commands with no payload. Recoverable transport failures are converted to
`Response` failures rather than thrown through the device API; static parsers
return typed failures as well.

| Domain type | Valid values |
|---|---|
| `WeeklyTemperatureSetpointCelsius` | 15-30 °C |
| `ActiveWeeklyTemperature` | `VENTILATION_ONLY` or a `WeeklyTemperatureSetpointCelsius`; never use 0 °C as a sentinel |
| `SupplyAirTargetCelsius` | 10-40 °C; S21 input register 48 is raw 100-400, scaled by 10 |
| `MvhrTemperatureCelsius` | -50 to 100 °C as an application sanity bound for sensor readings; this bound is a software policy, not a manufacturer limit |
| `FanSpeedPercent` | 0-100% |
| `FanRpm` | 0-5000 rpm |
| `AirflowRate` | 0-10000 m³/h |
| `DuctPressurePascal` | 0-10000 Pa |
| `RelativeHumidityPercent` | 0-100% |
| `CarbonDioxidePpm` | 0-10000 ppm |
| `Pm25Concentration` | 0-1000 µg/m³ |
| `VocPercent` | 0-100% |
| `FanMode` | Validated value object for `SPEED_1` through `SPEED_5` or `MANUAL`; wire value `255` is emitted only by the Modbus mapping |

The repository's shared
[`TemperatureCelsius`](../../src/main/java/com/oodesigns/devicecomms/domain/value/TemperatureCelsius.java)
currently permits values up to 1000 °C. That broad type should not be used for
an MVHR schedule setpoint. The MVHR-specific type rejects values such as 1000 °C
and fractional degrees because the S21 schedule field is a whole-degree byte.
The current Blauberg value objects are implemented and boundary-tested.

## Configure and Use

Create a JSON profile for the selected transport. TCP:

```json
{
  "transport": "modbus-tcp",
  "host": "192.0.2.10",
  "port": 502,
  "timeoutMillis": 2000
}
```

For RTU, use `transport: "modbus-rtu"`, `serialPort: "/dev/ttyUSB0"`,
`baudRate: 9600`, and `timeoutMillis: 1000`. Configure the registered Modbus
factory with the controller's Unit ID; the factory default is currently 1.

The `blauberg-mvhr` plugin is registered through `ServiceLoader` and created
through `DeviceFactory`/`JsonConfigFactory`. After checking the returned
`Response<Device>`, use the typed command and query API:

```java
DeviceType deviceType = new DeviceType("blauberg-mvhr");
Dependencies dependencies = new Dependencies(
  new CommunicationClientRegistry(new ServiceLoaderProviderCatalog()), Map.of());
DeviceFactory deviceFactory = new DeviceFactory(new ServiceLoaderDeviceProviderCatalog(),
  new JsonConfigFactory(Map.of(deviceType, Path.of("config/blauberg-mvhr.json"))),
  dependencies);
Response<Device> created = deviceFactory.createDevice(deviceType);
if (created.status() == Response.Status.OK) {
    BlaubergMVHR device = (BlaubergMVHR) created.value();
    device.open().join();
    device.execute(new SetControllerPower(true)).join();
    device.execute(new SetUnitOnOff(true)).join();
    device.execute(new SetFanMode(FanMode.SPEED_3)).join();
    device.execute(new SetManualFanSpeed(new FanSpeedPercent(55))).join();
    device.execute(new SetSensorEnabled(Sensor.INTERNAL_CO2, true)).join();
    device.execute(new SetPresetAirflow(new PresetAirflowSettings(
      FanPreset.SPEED_2, new AirflowRate(450), new AirflowRate(420)))).join();
    device.execute(new UpdateWeeklyTemperature(
      new ScheduleSlot(ScheduleDay.MONDAY, new SchedulePeriodNumber(1)),
      new WeeklyTemperatureSetpointCelsius(22))).join();
    Response<MvhrSnapshot> snapshot = device.read(new ReadSnapshot()).join();
    device.close().join();
}
```

The implemented snapshot includes operation flags, fan mode/weekly speed, RPM,
airflow and pressure, selected/supply/outdoor temperatures, calculated supply
target, internal RH/CO2/PM2.5/VOC readings, active weekly temperature mode, and
filter condition. External sensor channels remain in the S21 map but are not yet
included in the snapshot.

Command translations include:

1. `SetControllerPower(false)` writes `false` to `CL_POWER` (coil 0), distinct from `CL_TIMER` (coil 1) for unit on/off.
2. `SetFanMode(FanMode.SPEED_3)` writes `3` to holding register 2.
3. `SetManualFanSpeed(FanSpeedPercent(55))` writes `255` to register 2, then the wrapped `55%` value to register 17. Failure of the first write prevents the second.
4. `SetPresetAirflow` writes the supply and extract values to the paired preset registers, stopping if the first write fails.
5. `UpdateWeeklyTemperature` reads the schedule word, preserves its fan-speed high byte, replaces the temperature low byte, and fails without writing if the existing word is invalid.

## Before Connecting

Use the exact register table and connection parameters for the unit/controller
firmware. The S21 document states that RTU operation over RS-485 requires
disconnecting wired control panels on that interface. It also lists TCP port
502 and a default TCP unit ID may vary by installation, so verify the configured
unit ID, serial settings, and controller network settings before sending writes.