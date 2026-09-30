package com.oodesigns.devicecomms.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

class ResponseTest {
    @Test
    void mapsSuccessfulValues() {
        final Response<String> response = Response.success("temperature");

        assertEquals("TEMPERATURE", response.map(String::toUpperCase).value());
    }

    @Test
    void flatMapsSuccessfulValues() {
        final Response<Integer> response = Response.success(25)
                .flatMap(value -> Response.success(value * 10));

        assertEquals(250, response.value());
    }

    @Test
    void failurePassesThroughMapAndFlatMapUnchanged() {
        final Response<String> failure = Response.failure("device unavailable");

        assertSame(failure, (Object) failure.map(String::length));
        assertSame(failure, (Object) failure.flatMap(value -> Response.success(value.length())));
        assertEquals(Response.Status.EXCEPTION, failure.status());
        assertEquals("device unavailable", failure.details());
    }

    @Test
    void foldSelectsTheMatchingBranch() {
        final Response<String> success = Response.success("ready");
        final Response<String> failure = Response.failure("offline");

        assertEquals("READY", success.fold(String::toUpperCase, ignored -> "FAILED"));
        assertEquals("FAILED", failure.fold(String::toUpperCase, ignored -> "FAILED"));
    }
}
