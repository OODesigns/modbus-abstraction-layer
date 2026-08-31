package io.oodesigns.modbus.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Response")
class ResponseTest {

    @Test
    @DisplayName("a success carries its value and an OK status")
    void successCarriesValue() {
        Response<Integer> response = Response.success(42);

        assertEquals(Status.OK, response.status());
        assertTrue(response.isSuccess());
        assertFalse(response.isFailure());
        assertEquals(42, response.value().orElseThrow());
        assertEquals("", response.details());
    }

    @Test
    @DisplayName("a void success has no value but is still OK")
    void voidSuccessHasNoValue() {
        Response<Void> response = Response.success();

        assertEquals(Status.OK, response.status());
        assertTrue(response.value().isEmpty());
    }

    @Test
    @DisplayName("a failure carries details and an EXCEPTION status")
    void failureCarriesDetails() {
        Response<Integer> response = Response.failure("device offline");

        assertEquals(Status.EXCEPTION, response.status());
        assertTrue(response.isFailure());
        assertEquals("device offline", response.details());
        assertTrue(response.value().isEmpty());
    }

    @Test
    @DisplayName("failure details are a construction precondition")
    void failureDetailsMustBeMeaningful() {
        assertThrows(IllegalArgumentException.class, () -> Response.failure(null));
        assertThrows(IllegalArgumentException.class, () -> Response.failure("  "));
    }

    @Test
    @DisplayName("a success value is a construction precondition")
    void successValueMustNotBeNull() {
        assertThrows(IllegalArgumentException.class, () -> Response.success(null));
    }

    @Test
    @DisplayName("map transforms the value of a success")
    void mapTransformsSuccess() {
        Response<String> mapped = Response.success(21).map(v -> "value=" + (v * 2));

        assertEquals("value=42", mapped.value().orElseThrow());
    }

    @Test
    @DisplayName("map cascades a failure unchanged")
    void mapCascadesFailure() {
        Response<Integer> failure = Response.failure("bus error");

        Response<String> mapped = failure.map(v -> "never called");

        assertEquals(Status.EXCEPTION, mapped.status());
        assertEquals("bus error", mapped.details());
    }

    @Test
    @DisplayName("map never lets an exception escape the Response boundary")
    void mapConvertsThrownExceptionIntoFailure() {
        Response<String> mapped = Response.success(1).map(v -> {
            throw new IllegalStateException("boom");
        });

        assertEquals(Status.EXCEPTION, mapped.status());
        assertTrue(mapped.details().contains("boom"));
    }

    @Test
    @DisplayName("flatMap composes responses and cascades failures")
    void flatMapComposes() {
        Response<Integer> success = Response.success(4);

        assertEquals(8, success.flatMap(v -> Response.success(v * 2)).value().orElseThrow());
        assertEquals("no reading", success.flatMap(v -> Response.<Integer>failure("no reading")).details());

        Response<Integer> failure = Response.failure("upstream");
        assertEquals("upstream", failure.flatMap(v -> Response.success(v * 2)).details());
    }

    @Test
    @DisplayName("flatMap never lets an exception escape the Response boundary")
    void flatMapConvertsThrownExceptionIntoFailure() {
        Response<Integer> mapped = Response.success(1).flatMap(v -> {
            throw new IllegalStateException("kaboom");
        });

        assertTrue(mapped.isFailure());
        assertTrue(mapped.details().contains("kaboom"));
    }

    @Test
    @DisplayName("fold selects the branch matching the status")
    void foldSelectsBranch() {
        assertEquals("ok:7", Response.success(7).fold(v -> "ok:" + v, d -> "err:" + d));
        assertEquals("err:down", Response.<Integer>failure("down").fold(v -> "ok:" + v, d -> "err:" + d));
    }

    @Test
    @DisplayName("a cascaded failure keeps its identity-preserving details")
    void cascadedFailureKeepsDetails() {
        Response<int[]> original = Response.failure("Modbus exception code 2");

        Response<Double> cascaded = original.map(v -> (double) v[0]).map(v -> v * 10);

        assertEquals("Modbus exception code 2", cascaded.details());
    }

    @Test
    @DisplayName("orElse falls back when there is no value")
    void orElseFallsBack() {
        assertEquals(5, Response.success(5).orElse(0));
        assertEquals(0, Response.<Integer>failure("nope").orElse(0));
    }

    @Test
    @DisplayName("failures of any type can be re-typed without losing details")
    void failuresCanBeRetyped() {
        Response<Integer> failure = Response.failure("no route to host");
        Response<String> retyped = failure.asFailure();

        assertEquals("no route to host", retyped.details());
        assertSame(Status.EXCEPTION, retyped.status());
    }
}
