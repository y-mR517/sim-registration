package com.example.sim_registration.exception;

import org.springframework.http.HttpStatus;

public class MnoNumberUnavailableException extends ApiException {
    public MnoNumberUnavailableException() {

        super(HttpStatus.CONFLICT, "NUMBER_UNAVAILABLE",
                "This number is not available for registration");
    }
}
