package com.example.sim_registration.dto.response;

import com.example.sim_registration.entity.SimRegistration;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RegistrationResult {

    private SimRegistration registration;

    //if true = registration was newly created
    // false = existing registration returned because of idempotency

    private boolean created;
}