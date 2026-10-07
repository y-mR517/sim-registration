package com.example.sim_registration.exception;

import org.springframework.http.HttpStatus;

public class NidaTimeoutException extends NidaException {
    public NidaTimeoutException() {
        super(HttpStatus.GATEWAY_TIMEOUT,"NIDA_TIMEOUT", "Identity verification timed out. Please try again in a few minutes");
    }

    public NidaTimeoutException(Throwable cause){
        this();
        initCause(cause);
    }
}
