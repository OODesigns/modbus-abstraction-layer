# Waveshare Modbus RTU Analog Input 8CH

Reference notes transcribed from the [Waveshare product page](https://www.waveshare.com/wiki/Modbus_RTU_Analog_Input_8CH) and its Development Protocol V2. Confirm the hardware revision and firmware against the original documentation before commissioning.

![Waveshare Modbus RTU Analog Input 8CH](https://www.waveshare.com/w/upload/thumb/4/4b/Modbus_RTU_Analog_Input_8CH.jpg/300px-Modbus_RTU_Analog_Input_8CH.jpg)

## Hardware and Versions

The module provides eight individually configurable analog input channels. Each channel has `AIN+` and `AIN-` terminals and supports differential or single-ended wiring. For single-ended input, connect `AIN-` to ground. When using different power sources, establish a common ground or readings may be inaccurate.

Internal jumpers correspond to channels `AI1` through `AI8`:

- Voltage measurement: disconnect the corresponding jumper.
- Current measurement: connect the corresponding jumper.
- Version A defaults to current mode with jumpers connected.
- Version B defaults to voltage mode with jumpers disconnected.

Do not change a channel between voltage and current without setting the jumper for that channel. Incorrect jumper configuration can produce inaccurate measurements.

![Waveshare analog-input channel jumpers](https://www.waveshare.com/w/upload/4/4c/Modbus-RTU-AI-8CH-06-1.jpg)
![Waveshare Modbus RTU Analog Input 8CH hardware overview](https://www.waveshare.com/w/upload/a/ae/Modbus-RTU-Analog-Input-8CH-Overview.png)

| Property | Modbus RTU Analog Input 8CH (A) | Modbus RTU Analog Input 8CH (B) |
|---|---|---|
| Default mode | 8-channel 0-20 mA current | 8-channel 0-10 V voltage |
| Supported voltage ranges | 0-5 V, 1-5 V | 0-10 V, 2-10 V |
| Supported current ranges | 0-20 mA, 4-20 mA | 0-20 mA, 4-20 mA |
| Resolution | 12-bit | 12-bit |
| Current sampling resistor | 249 ohm | 499 ohm |
| Operational amplifier ratio | 32.4/49.9 | 10/32.4 |
| Channels | 8 analog inputs | 8 analog inputs |

![Waveshare 8CH A/B version comparison](https://www.waveshare.com/w/upload/0/01/Modbus-RTU-Analog-Input-AB-Compare-01.jpg)

The module also has a scale-code range mode. Its raw ADC code must be converted using the configured module revision's operational amplifier ratio:

```text
Voltage = ScaleCode * 3300 / 4095 / OperationalAmplifierRatio
Current = Voltage / SamplingResistor
```

## Modbus Connection

The documented serial defaults are:

| Setting | Default |
|---|---|
| Baud rate | 115200 (listed as default in the protocol table) |
| Data bits | 8 |
| Parity | None |
| Stop bits | 1 |
| Device address | 1 (example address; configurable) |

The SSCOM and Modbus Poll walkthroughs use 9600 baud as their test setting;
that does not agree with the protocol table's 115200 default. Use the module's
configured baud rate, or change it using the documented UART parameter command.
The vendor text also conflicts on parity codes `0x01` and `0x02`: one section
labels them even/odd, while another labels them odd/even. Verify the actual
firmware/manual for the module before selecting parity other than none.

Connect RS-485 `A` to `A` and `B` to `B`. Do not connect RS-485 directly to a Raspberry Pi UART; use a suitable RS-485 level converter or HAT. A 120 ohm termination resistor across A/B may be needed at a bus end.

![Waveshare RS-485 wiring](https://www.waveshare.com/w/upload/b/b3/Modbus_RTU_Analog_Input_8CH-03.jpg)

## Input Modes

Each channel's data type is configured independently by a holding register.

| Mode | Input | Reported value | Unit |
|---:|---|---:|---|
| `0x0000` | A: 0-5 V; B: 0-10 V | A: 0-5000; B: 0-10000 | mV |
| `0x0001` | A: 1-5 V; B: 2-10 V | A: 1000-5000; B: 2000-10000 | mV |
| `0x0002` | 0-20 mA | 0-20000 | uA |
| `0x0003` | 4-20 mA | 4000-20000 | uA |
| `0x0004` | ADC scale code | 0-4095 | Code; convert to voltage/current if needed |

These are the documentation's stated ranges. Verify the exact version and firmware because the two hardware versions have different voltage ranges and analog front-end ratios.
The vendor table lists the scale-code range as `0-4096`, but calls the converter
12-bit and its conversion formula divides by `4095`; this reference uses the
12-bit maximum, `4095`, as the accepted scale-code limit.

## Register Map

The product documentation uses `3x` for input registers and `4x` for holding registers. The start addresses below are the protocol addresses shown in the register map; use zero-based Modbus PDU addresses when constructing requests.

| Reference | Address | Description | Access | Function code |
|---|---:|---|---|---|
| `3x0000`-`3x0007` | `0x0000`-`0x0007` | Channels 1-8 analog input values, unsigned 16-bit, high byte first | Read | `0x04` Read Input Registers |
| `4x1000`-`4x1007` | `0x1000`-`0x1007` | Channels 1-8 input range/type, values `0x0000`-`0x0004` | Read/write | `0x03`, `0x06`, `0x10` |
| `4x2000` | `0x2000` | UART settings; high byte parity, low byte baud-rate code | Read/write | `0x03`, `0x06` |
| `4x4000` | `0x4000` | Device address, `0x0001`-`0x00FF` | Read/write | `0x03`, `0x06` |
| `4x8000` | `0x8000` | Software version, e.g. `0x0064` means V1.00 | Read | `0x03` |

### UART Parameter Encoding

| Field | Values |
|---|---|
| Parity, high byte | `0x00` none, `0x01` even, `0x02` odd |
| Baud-rate code, low byte | `0x00` 4800, `0x01` 9600, `0x02` 19200, `0x03` 38400, `0x04` 57600, `0x05` 115200, `0x06` 128000, `0x07` 256000 |

### Exception Codes

The module documents Modbus exception responses with the original function code plus `0x80` and a one-byte exception code:

| Code | Name | Meaning |
|---:|---|---|
| `0x01` | Illegal Function | Function code is not supported |
| `0x02` | Illegal Data Address | Requested data address is invalid |
| `0x03` | Illegal Data Value | Requested value or operation is invalid |
| `0x04` | Server Failure | Device failure |
| `0x05` | Acknowledge | Request accepted and still processing |
| `0x06` | Device Busy | Device cannot perform the request now |

## Modbus Requests

The following frames are from the documentation for device address `0x01`. RTU frames end with CRC16, low byte first.

### Read Eight Input Channels

Function `0x04`, starting at input register `0x0000`, quantity eight:

```text
01 04 00 00 00 08 F1 CC
```

The response contains eight unsigned 16-bit channel values, most-significant byte first, followed by CRC16. The manual's sample response is:

```text
01 04 10 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 55 2C
```

### Read Eight Channel Modes

Function `0x03`, starting at holding register `0x1000`, quantity eight:

```text
01 03 10 00 00 08 40 CC
```

Each returned word gives one channel's mode. The documented sample returns `0x0002` for each channel, meaning 0-20 mA.

### Set One Channel Mode

Function `0x06`, write mode `0x0003` (4-20 mA) to channel 1 at `0x1000`:

```text
01 06 10 00 00 03 CD 0B
```

To set channel 2 instead, the register address is `0x1001`. The product page labels one example “Read data type” even though it uses function `0x06`; that command is a write.

### Set All Eight Channel Modes

Function `0x10`, write `0x0003` (4-20 mA) to all eight mode registers beginning at `0x1000`:

```text
01 10 10 00 00 08 10 00 03 00 03 00 03 00 03 00 03 00 03 00 03 00 03 91 2B
```

### Change Serial Settings

UART parameters are written to `0x2000`. For example, the manual's frame selects no parity and baud-rate code `0x05` (115200):

```text
00 06 20 00 00 05 43 D8
```

The example uses broadcast address `0x00`; a broadcast write does not return a normal slave response.

### Change Device Address

Write `0x0002` to `0x4000` to change the address to `0x02`:

```text
00 06 40 00 00 02 1C 1A
```

### Read Device Address

Read one holding register at `0x4000`:

```text
00 03 40 00 00 01 90 1B
```

### Read Software Version

Read one holding register at `0x8000`:

```text
00 03 80 00 00 01 AC 1B
```

The documented example response returns `0x0064`, representing version V1.00.

## CRC and Troubleshooting

Use Modbus RTU CRC16 for every non-broadcast request. The Waveshare serial assistant instructions recommend entering the command bytes without CRC when its ModbusCRC16 option is enabled; the assistant appends the checksum.

If there is no response, verify the slave address, baud rate, parity, A/B polarity, power, CRC, and jumper position. Ensure the channel is configured for the same voltage/current range as the physical wiring. A common ground is important when using separate supplies.

The vendor also provides [SSCOM](https://files.waveshare.com/wiki/common/Sscom5.13.1.zip), [Modbus Poll](https://www.modbustools.com/download.html), and demo code on its [product page](https://www.waveshare.com/wiki/Modbus_RTU_Analog_Input_8CH).

## Device Component

The project provides `AnalogInput8CH` as a read-only `Device` and as
the smaller `AnalogInputReader` component interface. A composite device can
depend on `AnalogInputReader` and combine its eight readings with other sensor
sources without depending on Modbus point addresses.

The implementation keeps `AnalogInput8CH` at the
`device.waveshare.analoginput` package root and the `AnalogInputReader` port in
`device.waveshare.analoginput.port`. Queries, returned channel
readings, validated electrical values, hardware profiles, plugin configuration,
and raw register protocol types are grouped in the `query`, `reading`, `value`,
`profile`, `configuration`, and `protocol` subpackages respectively.

Register the device with a profile such as:

```json
{
	"transport": "modbus-rtu",
	"serialPort": "/dev/ttyUSB0",
	"baudRate": 115200,
	"timeoutMillis": 1000,
	"moduleRevision": "A"
}
```

Set `moduleRevision` to `A` or `B`; this selects the validation limits for
voltage modes. The `waveshare-modbus-rtu-analog-input-8ch` plugin is available
through the existing `DeviceFactory`/`ServiceLoader` path. A parent device that
owns an already configured component can depend on the component port:

```java
final class CompositeSensorDevice {
		private final AnalogInputReader analogInputs;

		CompositeSensorDevice(final AnalogInputReader analogInputs) {
				this.analogInputs = analogInputs;
		}

		CompletableFuture<Response<AnalogInputSnapshot>> readAnalogSensors() {
				return analogInputs.readChannels();
		}
}
```

`readChannels()` reads all eight input registers and all eight channel-mode
registers. Each `AnalogChannelReading` contains a validated channel number, the
decoded `AnalogInputRangeMode`, and one mode-specific measurement:

- `VoltageMillivolts` for voltage modes, checked against revision A or B range.
- `CurrentMicroamps` for 0-20 mA and 4-20 mA modes.
- `AdcScaleCode` for direct scale-code mode. Converting that code to volts or
	current still requires the channel's physical jumper/input wiring information.

Unrecognized mode codes or readings outside the selected mode's range return a
failed `Response`; they are not exposed as plausible sensor values. The reader
does not configure channel modes or modify the module; configure those settings
separately using the documented holding registers and hardware jumpers.
