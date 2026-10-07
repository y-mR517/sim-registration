package com.example.sim_registration.exception;

import org.springframework.http.HttpStatus;

public class IdentityVerificationFailedException extends ApiException {
    public IdentityVerificationFailedException() {

      super(HttpStatus.UNPROCESSABLE_ENTITY, "IDENTITY_VERIFICATION_FAILED", "We could not verify the details provided. Please check them again.");
    }
}
