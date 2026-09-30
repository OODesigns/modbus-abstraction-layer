# Waveshare Modbus Ethernet Relay 30CH

The Waveshare Modbus Ethernet Relay is an Ethernet-connected relay module controlled through Modbus. The device can be powered through IEEE 802.3af PoE, its DC barrel jack, or its 7–36 V screw-terminal input. The module supports Modbus RTU and Modbus TCP configurations through its Ethernet serial-server gateway.

![Waveshare Modbus Ethernet Relay 30CH](https://www.waveshare.com/w/upload/thumb/9/92/Modbus_POE_ETH_Relay_30CH.jpg/300px-Modbus_POE_ETH_Relay_30CH.jpg)

> **Electrical safety:** Relay circuits can switch hazardous voltages and currents. Installation, wiring, maintenance, and load selection must be handled by qualified personnel. Isolate power before wiring or servicing; use correctly rated circuit protection; provide suitable overcurrent and inductive-load suppression; and do not exceed the relay contact ratings.

## Source Specification Note

The supplied product page is internally inconsistent: its title and protocol map describe 30 channels, with relay coils at `0x0000`–`0x001D`, while one specification row states 8 channels. The device implementation follows the 30-channel protocol map and product title. Confirm the channel count against the exact purchased hardware revision before connecting or controlling loads.

The source lists relay contact capacity as up to 10 A at 250 V AC or 10 A at 30 V DC. Verify ratings for the actual load and installation; inductive starting current can exceed the steady-state load current.

## Power and Network Setup

Connect the module to the LAN using Ethernet. Power it with IEEE 802.3af PoE or an external 7–36 V DC supply through the specified input. The board uses a TCP server by default and its serial-side settings are documented as 115200 baud, 8 data bits, no parity, 1 stop bit; the source advises not changing those defaults.

The factory/default transfer mode is transparent Modbus RTU over the Ethernet gateway. The project device currently uses the project's Modbus TCP `CommunicationClient`; configure the module's transfer protocol as **Modbus TCP** and its gateway type as **multi-host non-storage**. The module's Modbus TCP mode uses port 502. The stored-gateway mode can issue extra queries and may interfere with controller responses, so the product page recommends non-storage mode.

The device plugin expects a TCP profile such as:

```json
{
  "transport": "modbus-tcp",
  "host": "192.0.2.45",
  "port": 502,
  "timeoutMillis": 1500
}
```

Configure a static or reserved address appropriate to the local network. The address `192.0.2.45` above is an example only. The relay uses Modbus unit address 1 in the current transport configuration.

Transparent Modbus RTU-over-TCP mode is not configured by this plugin: the current transport registry provides Modbus TCP and serial Modbus RTU clients, but not an RTU-over-TCP client.

## Modbus Protocol

The device's relay control interface uses these functions:

| Function | Purpose |
|---:|---|
| `0x01` | Read coil status |
| `0x05` | Write one coil |
| `0x0F` | Write multiple coils |
| `0x03` | Read device address and software version registers |
| `0x06` | Write communication parameters |

The project device currently reads relay status using `0x01` and switches one relay at a time using `0x05`.

## Relay Addressing and State

The protocol maps relay channel 1 to coil address `0x0000` and channel 30 to `0x001D`:

| Relay channel | Coil address |
|---:|---:|
| 1 | `0x0000` |
| 2 | `0x0001` |
| ... | ... |
| 30 | `0x001D` |

The following CRC-bearing frames are **Modbus RTU reference examples** from the
device protocol. The project plugin configures Modbus TCP, whose adapter adds
MBAP framing and does not send RTU CRC bytes. The current adapter also reads
requested coil points through the shared client API; it does not emit the
quantity-30 RTU request shown in this vendor reference.

Single-coil RTU reference commands use these values:

| Data value | Meaning |
|---:|---|
| `0xFF00` | Energize relay (ON) |
| `0x0000` | De-energize relay (OFF) |
| `0x5500` | Toggle relay |

Example: switch relay 1 ON:

```text
01 05 00 00 FF 00 8C 3A
```

Switch relay 1 OFF:

```text
01 05 00 00 00 00 CD CA
```

Example: switch relay 2 ON:

```text
01 05 00 01 FF 00 DD FA
```

A single-coil write is echoed by the device. The project API currently exposes explicit `ON` and `OFF` states; it deliberately does not expose toggle because the shared Modbus coil API models coil values as booleans, not the protocol's special `0x5500` command.

## Read Relay Status

The vendor's single-request Modbus RTU example reads all 30 relay coils with
function `0x01`:

```text
01 01 00 00 00 1E BC 02
```

The returned bit field is little-endian within the byte stream: bit 0 represents the first requested relay, bit 1 the second, and so on. Unused high bits are zero. The project `Relay30CH` device returns an ordered `RelaySnapshot` of typed `RelayReading` values for channels 1–30. Its current `CommunicationClient` contract reads individual typed points, so the adapter may perform these as separate coil reads rather than one packed quantity-30 request.

## Write Multiple Relay States

The protocol supports function `0x0F` to write a contiguous bit field. For example, the supplied page shows all 30 relays ON with:

```text
01 0F 00 00 00 1E 04 FF FF FF 3F C1 92
```

Function `0x0F` is not currently available through the shared `CommunicationClient`, which only exposes single-point writes. The project's `SetRelayStates` command therefore writes each requested channel sequentially using `0x05`, stopping at the first failed write and identifying the channel whose write failed. This is not atomic: earlier channels may already have changed when a later write fails. If a write times out, the final state of that relay may be uncertain until read back.

## Flash and Toggle Commands

The device protocol also documents toggle (`0x5500`) and flash-on/flash-off commands through special function-`0x05` address/value encodings. They are not currently exposed by the project device API. Use explicit ON/OFF operations until a typed protocol command and transport support for these special encodings is added.

## Status Indicators

- `RUN`: Ethernet operation indicator, described as a two-second period square wave when running.
- `STA`: MCU activity indicator.
- `TXD` / `RXD`: serial transmit / receive activity.
- Ethernet green LED: TCP connection status.
- Ethernet yellow LED: network data activity.

## Configuration Tools and Resources

- [Product wiki](https://www.waveshare.com/wiki/Modbus_POE_ETH_Relay_30CH)
- [VirCom configuration tool](https://files.waveshare.com/wiki/common/VirCom_en.rar)
- [Modbus Poll](https://www.modbustools.com/download.html)
- [SSCOM relay utility](https://files.waveshare.com/wiki/Modbus%20POE%20ETH%20Relay/Sscom5.13.1_for_Modbus_POE_ETH_Relay.zip)
- [Raspberry Pi demo archive](https://files.waveshare.com/wiki/Modbus-POE-ETH-Relay-30CH/Modbus_POE_ETH_Relay_30CH_Code.zip)
- [Modbus protocol specification](https://www.waveshare.com/wiki/Modbus_Protocol_Specification)
- [RS485-to-Ethernet gateway manual](https://www.waveshare.com/wiki/Template:RS485_TO_ETH_(B)_Manual)

The source page also lists an operating temperature range of -15 to +70 °C. Follow the hardware manual for installation environment, enclosure, isolation, protection, and wiring requirements.
