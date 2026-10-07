package com.example.sim_registration.exception;

import org.springframework.http.HttpStatus;

public class InvalidStateException extends ApiException {
    public InvalidStateException() {

        super(HttpStatus.CONFLICT, "INVALID_STATE", "This registration cannot be approved in its current state.");
    }
}
