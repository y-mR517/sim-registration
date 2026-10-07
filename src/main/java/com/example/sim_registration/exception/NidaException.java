package com.example.sim_registration.exception;

import org.springframework.http.HttpStatus;

public abstract class NidaException extends ApiException {
    protected NidaException(HttpStatus status, String code , String message ){
        super(status, code, message);
    }
}


