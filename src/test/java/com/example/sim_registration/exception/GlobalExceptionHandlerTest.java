package com.example.sim_registration.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleUnexpected_whenSpringWebError_keepsItsOwnStatus() {
        var response = handler.handleUnexpected(new ResponseStatusException(HttpStatus.NOT_FOUND));

        assertEquals(404, response.getStatusCode().value());
        assertEquals("REQUEST_ERROR", response.getBody().code());
    }

    @Test
    void handleUnexpected_whenUnknownError_returns500() {
        var response = handler.handleUnexpected(new RuntimeException("boom"));

        assertEquals(500, response.getStatusCode().value());
        assertEquals("INTERNAL_ERROR", response.getBody().code());
    }
}