package com.oodesigns.devicecomms.domain.value;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

public record IPAddress(String value) {
    public IPAddress {
        Objects.requireNonNull(value, "value");
        if (!isValid(value)) {
            throw new IllegalArgumentException("value must be an IPv4 or IPv6 address");
        }
    }

    private static boolean isValid(final String address) {
        if (address.isBlank()) {
            return false;
        }
        if (address.indexOf(':') < 0) {
            final String[] octets = address.split("\\.", -1);
            if (octets.length != 4) {
                return false;
            }
            for (final String octet : octets) {
                if (octet.isEmpty() || !octet.chars().allMatch(Character::isDigit)) {
                    return false;
                }
                try {
                    if (Integer.parseInt(octet) > 255) {
                        return false;
                    }
                } catch (final NumberFormatException exception) {
                    return false;
                }
            }
            return true;
        }
        if (!address.matches("[0-9A-Fa-f:.]+")) {
            return false;
        }
        try {
            return InetAddress.getByName(address).getAddress().length == 16;
        } catch (final UnknownHostException exception) {
            return false;
        }
    }
}