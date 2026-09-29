package com.oodesigns.modbus.domain.transformer;

import com.oodesigns.modbus.domain.Response;

public interface ResponseTransformer<R, V> {
    Response<V> transform(Response<R> raw);
}