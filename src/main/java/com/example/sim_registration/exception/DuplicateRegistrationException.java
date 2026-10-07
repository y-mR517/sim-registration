package com.example.sim_registration.exception;

import org.springframework.http.HttpStatus;

public class DuplicateRegistrationException extends ApiException {
    public DuplicateRegistrationException(){
        super(HttpStatus.CONFLICT, "DUPLICATE_SIM", "This number is already registered");
    }
}
