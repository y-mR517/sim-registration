package com.example.sim_registration.exception;

import org.springframework.http.HttpStatus;

public class NidaUnavailableException extends NidaException {

    public NidaUnavailableException() {

        super(HttpStatus.SERVICE_UNAVAILABLE, "NIDA_UNAVAILABLE","Identity service is temporaliy unavailable. Please try again later.");
    }
}
