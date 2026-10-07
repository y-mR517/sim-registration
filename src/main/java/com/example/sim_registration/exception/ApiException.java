package com.example.sim_registration.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
@Setter
@Getter

//ApiException carries a status and code so that the handler does not have to guess

public abstract class ApiException extends RuntimeException {

    private final HttpStatus status;
    private final String code;

    protected ApiException(HttpStatus status, String code, String mesage){
        super(mesage);
        this.status = status;
        this.code = code;
    }

}
