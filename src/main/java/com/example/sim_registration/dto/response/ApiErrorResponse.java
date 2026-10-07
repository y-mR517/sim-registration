package com.example.sim_registration.dto.response;

import java.time.Instant;

public record ApiErrorResponse(String code, String message, int status, Instant timestamp) {
}
