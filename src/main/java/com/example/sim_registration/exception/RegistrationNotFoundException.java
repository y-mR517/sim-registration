package com.example.sim_registration.exception;

import org.springframework.http.HttpStatus;

public class RegistrationNotFoundException extends ApiException {
    public RegistrationNotFoundException() {
        super(HttpStatus.NOT_FOUND, "REGISTRATION_NOT_FOUND", "Registration not found");
    }
}

