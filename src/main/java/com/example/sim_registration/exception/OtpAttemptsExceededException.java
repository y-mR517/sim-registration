package com.example.sim_registration.exception;

import org.springframework.http.HttpStatus;

public class OtpAttemptsExceededException extends ApiException {
    public OtpAttemptsExceededException() {

        super(HttpStatus.TOO_MANY_REQUESTS, "OTP_ATTEMPTS_EXCEEDED", "Too many incorrect attempts. Please start a new registration.");
    }
}
