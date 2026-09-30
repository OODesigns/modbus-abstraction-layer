package com.oodesigns.devicecomms.domain.device;

import java.util.Map;
import java.util.Objects;

public record ConfigLoader(Map<String, Object> values) {
    public ConfigLoader {
        Objects.requireNonNull(values, "values");
        values = Map.copyOf(values);
    }
}