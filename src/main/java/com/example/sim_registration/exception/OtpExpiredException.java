package com.example.sim_registration.exception;

import org.springframework.http.HttpStatus;

public class OtpExpiredException extends ApiException {
    public OtpExpiredException() {
        super(HttpStatus.GONE, "OTP_EXPIRED", "The code has expired. Please request a new one.");
    }
}
