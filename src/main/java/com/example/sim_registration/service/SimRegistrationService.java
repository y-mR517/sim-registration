package com.example.sim_registration.service;

import com.example.sim_registration.dto.request.StartRegistrationRequest;
import com.example.sim_registration.dto.response.RegistrationResult;
import com.example.sim_registration.dto.response.StartRegistrationResponse;
import com.example.sim_registration.entity.SimRegistration;

public interface SimRegistrationService {

    SimRegistration findByIdempotencyKey(String idempotencyKey);

    StartRegistrationResponse saveRegistration(
            StartRegistrationRequest request,
            String idempotencyKey
    );
}