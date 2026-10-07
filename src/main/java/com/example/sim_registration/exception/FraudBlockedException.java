package com.example.sim_registration.exception;

import org.springframework.http.HttpStatus;

//keeping a vague message so that the fraudster can not tell which rule caught them

public class FraudBlockedException extends ApiException{
    public FraudBlockedException(){
        super(HttpStatus.FORBIDDEN, "REQUEST_BLOCKED", "This request can not be processed");
    }
}
