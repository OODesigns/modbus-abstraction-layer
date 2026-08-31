package io.oodesigns.modbus.value;
import java.net.InetAddress;
/** A syntactically valid IPv4 or IPv6 address. */
public record IPAddress(String value) {
 public IPAddress { if (value == null || value.isBlank()) throw new IllegalArgumentException("IP address is required"); try { InetAddress.getByAddress(value, InetAddress.getByName(value).getAddress()); if (!value.matches("[0-9a-fA-F:.]+")) throw new IllegalArgumentException("invalid IP address"); } catch (Exception e) { throw new IllegalArgumentException("invalid IP address"); } }
}
