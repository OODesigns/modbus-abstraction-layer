# Waveshare Modbus RTU Analog Output 8CH (B)

The Waveshare Modbus RTU Analog Output 8CH (B) is an RS-485 module with eight voltage outputs. Each output is configurable from 0 to 10 V by writing a millivolt value to its holding register.

> **Power requirement:** The input supply voltage must exceed the requested output voltage. Waveshare gives at least 15 V input as an example for producing a 10 V output. An insufficient supply can prevent the output reaching its requested level.

![Waveshare Modbus RTU Analog Output 8CH (B)](https://www.waveshare.com/w/upload/5/5e/Modbus_RTU_Analog_Output_8CH-b-01.jpg)

## Hardware

- `AO1` through `AO8` are the voltage outputs.
- `AGND` is analog ground.
- Output range: 0–10 V.
- Maximum output current: up to 100 mA per channel, depending on load and voltage. Check the module's actual drive capability for the intended load.
- Connect RS-485 `A` to `A` and `B` to `B`.
- Do not connect RS-485 directly to a Raspberry Pi UART; use an RS-485 level converter.

The product family has two versions:

| Version | Output type | Output range |
|---|---|---:|
| Modbus RTU Analog Output 8CH | Current | 0–20 mA |
| Modbus RTU Analog Output 8CH (B) | Voltage | 0–10 V |

This document describes the voltage-output `(B)` version.

## Modbus Functions

| Function code | Operation |
|---:|---|
| `0x03` | Read holding registers |
| `0x06` | Write one holding register |
| `0x10` | Write multiple holding registers |

Unless otherwise stated, examples use slave address `0x01`. Modbus RTU frames include CRC16; examples below show CRC bytes where supplied by the manufacturer.

## Register Map

| Register address | Contents | Access | Function codes |
|---:|---|---|---|
| `0x0000`–`0x0007` | Output values for channels 1–8, in mV | Read/write | `0x03`, `0x06`, `0x10` |
| `0x2000` | UART parameters | Read/write | `0x03`, `0x06` |
| `0x4000` | Device address | Read/write | `0x03`, `0x06` |
| `0x8000` | Software version | Read | `0x03` |

The documentation labels these as `4x0000` through `4x0007`, etc. In Modbus requests, use the corresponding zero-based register offsets shown above, subject to the client library's address convention.

Output values are unsigned register values expressed in millivolts. For example, `0x03E8` is decimal 1000, or 1 V. The output range is 0–10000 mV.

## Set One Output

Write one holding register with function `0x06`. Channel 1 uses address `0x0000`; channel 2 uses `0x0001`, continuing through channel 8 at `0x0007`.

| Desired output | Register value | Example request |
|---:|---:|---|
| 1 V on channel 1 | `0x03E8` (1000 mV) | `01 06 00 00 03 E8 89 74` |
| 5 V on channel 2 | `0x1388` (5000 mV) | `01 06 00 01 13 88 D5 5C` |

A successful single-register write is echoed by the device. For example, setting channel 1 to 1 V returns:

```text
01 06 00 00 03 E8 89 74
```

## Set Multiple Outputs

Write consecutive holding registers with function `0x10`. The start address selects the first channel. Register count must not exceed the available channels. Each output value is encoded as a 16-bit millivolt value, most-significant byte first.

Set all eight channels to 1 V:

```text
01 10 00 00 00 08 10 03 E8 03 E8 03 E8 03 E8 03 E8 03 E8 03 E8 03 E8 3C 05
```

Set channels 3–5 to 2 V:

```text
01 10 00 02 00 03 06 07 D0 07 D0 07 D0 84 0E
```

A successful multiple-register write returns the start address and register count, for example:

```text
01 10 00 00 00 08 C1 CF
```

## Read Output Values

Use function `0x03` to read output registers. The read value is the currently configured output in millivolts.

Read all eight channels:

```text
01 03 00 00 00 08 44 0C
```

Example response with all channels set to 1 V:

```text
01 03 10 03 E8 03 E8 03 E8 03 E8 03 E8 03 E8 03 E8 03 E8 C1 91
```

| Channels | Example request |
|---|---|
| 1–8 | `01 03 00 00 00 08 44 0C` |
| 1 | `01 03 00 00 00 01 84 0A` |
| 2 | `01 03 00 01 00 01 D5 CA` |
| 3–5 | `01 03 00 02 00 03 A4 0B` |

## Communication Settings

The module examples use 9600 baud, 8 data bits, and no parity. The device address and UART parameters are configurable through registers.

### Set baud rate and parity

Register `0x2000` configures parity in the high byte and baud-rate mode in the low byte:

- Parity: `0x00` none, `0x01` even, `0x02` odd.
- Baud-rate mode:

| Value | Baud rate |
|---:|---:|
| `0x00` | 4800 |
| `0x01` | 9600 |
| `0x02` | 19200 |
| `0x03` | 38400 |
| `0x04` | 57600 |
| `0x05` | 115200 |
| `0x06` | 128000 |
| `0x07` | 256000 |

Broadcast example setting no parity and 115200 baud:

```text
00 06 20 00 00 05 43 D8
```

### Set device address

Register `0x4000` stores the device address, from `0x0001` to `0x00FF`.

Set the address to `0x01`:

```text
00 06 40 00 00 01 5C 1B
```

### Read device address

```text
00 03 40 00 00 01 90 1B
```

Example response reporting address `0x01`:

```text
01 03 02 00 01 79 84
```

### Read software version

Register `0x8000` stores the version as an integer with two implied decimal places. For example, `0x0064` is decimal 100 and represents version 1.00.

```text
00 03 80 00 00 01 AC 1B
```

Example response:

```text
01 03 02 00 64 B9 AF
```

## Exceptions

Exception responses use the requested function code plus `0x80`, followed by an exception code and CRC. For example:

```text
01 85 03 02 91
```

Common Modbus exception codes:

| Code | Name | Meaning |
|---:|---|---|
| `0x01` | Illegal Function | Requested function is unsupported |
| `0x02` | Illegal Data Address | Register address is invalid |
| `0x03` | Illegal Data Value | Requested value or operation is invalid |
| `0x04` | Server Failure | Device failure |
| `0x05` | Acknowledge | Request received and still processing |
| `0x06` | Device Busy | Device cannot perform the request currently |

## Software and Demos

Waveshare lists SSCOM and Modbus Poll as serial Modbus test tools. Its demo archive includes Raspberry Pi, STM32, Arduino, and PLC examples:

- [Waveshare demo archive](https://files.waveshare.com/wiki/Modbus-RTU-Analog-Output-8CH/Modbus_RTU_Analog_Output_Code.zip)
- [SSCOM serial assistant](https://files.waveshare.com/upload/b/b3/Sscom5.13.1.zip)
- [Modbus Poll](https://www.modbustools.com/download.html)
- [Modbus protocol specification](https://www.waveshare.com/wiki/Modbus_Protocol_Specification)
- [Modbus series bootloader description](https://www.waveshare.com/wiki/Modbus_Series_BootLoader_Description)
- [Product wiki](https://www.waveshare.com/wiki/Modbus_RTU_Analog_Output_8CH_(B))

After the standard demos run successfully, all channels are set to 5 V. The Arduino and STM32 demos require an RS-485 CAN Shield or another appropriate RS-485 interface. The Raspberry Pi demo also requires an RS-485 level converter.

## Troubleshooting Notes

- Confirm slave address, baud rate, parity, RS-485 A/B polarity, CRC, and supply voltage.
- Ensure the output load is within the board's capability. The maximum current depends on load and voltage; the FAQ lists up to 100 mA per channel.
- If serial traffic is received but an output does not change, check whether the module is in linkage mode.
- If the board's communication settings are unknown, see the [bootloader description](https://www.waveshare.com/wiki/Modbus_Series_BootLoader_Description) for reading or resetting them.

## Project Device

The project exposes the module as `AnalogOutput8CH`, a read/write `Device`
registered under device type `waveshare-modbus-rtu-analog-output-8ch-b`. It
uses the configured Modbus RTU communication client and does not open its own
serial port outside the standard `Device` lifecycle.

Example configuration:

```json
{
	"transport": "modbus-rtu",
	"serialPort": "/dev/ttyUSB0",
	"baudRate": 9600,
	"timeoutMillis": 1000
}
```

Use `ReadAnalogOutputs` to read all eight holding registers. The result is an
`AnalogOutputSnapshot` containing ordered `AnalogOutputReading` values.
`OutputMillivolts` validates each value from 0 through 10000 mV, and
`AnalogOutputChannelNumber` validates channels 1 through 8.

Use `SetAnalogOutput` for one channel, or `SetAnalogOutputs` to provide a
validated list of channel/value pairs. Duplicate channels and empty multi-write
commands are rejected. The current `CommunicationClient` API supports only
single-register writes, so `SetAnalogOutputs` sends the entries sequentially
using function `0x06`; it stops at the first failed write. The protocol supports
function `0x10`, but that batch operation is not yet exposed by the shared
communication port. A failed multi-output operation can therefore leave
earlier channels updated and later channels unchanged.

For a multi-output command:

```java
new SetAnalogOutputs(List.of(
		new AnalogOutputSetting(new AnalogOutputChannelNumber(1), new OutputMillivolts(1000)),
		new AnalogOutputSetting(new AnalogOutputChannelNumber(2), new OutputMillivolts(5000))));
```
