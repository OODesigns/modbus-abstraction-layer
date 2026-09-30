package com.oodesigns.devicecomms.domain.transformer;

import com.oodesigns.devicecomms.domain.Response;

public interface ResponseTransformer<R, V> {
    Response<V> transform(Response<R> raw);
}