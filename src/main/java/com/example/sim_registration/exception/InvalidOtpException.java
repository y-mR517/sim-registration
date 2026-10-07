package com.example.sim_registration.exception;

import org.springframework.http.HttpStatus;

public class InvalidOtpException extends ApiException {
    public InvalidOtpException()
    {
        super(HttpStatus.BAD_REQUEST, "INVALID_OTP", "The code entered is incorrect");
    }
}
