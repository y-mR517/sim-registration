package com.example.sim_registration.service;

import com.example.sim_registration.dto.request.CompleteRegistrationRequest;
import com.example.sim_registration.dto.request.StartRegistrationRequest;
import com.example.sim_registration.dto.response.CompleteRegistrationResponse;
import com.example.sim_registration.dto.response.RegistrationStatusResponse;
import com.example.sim_registration.dto.response.StartRegistrationResponse;

import java.util.UUID;

public interface StartRegistrationService {

    StartRegistrationResponse startRegistration(
            StartRegistrationRequest request,
            String idempotencyKey
    );

    RegistrationStatusResponse getRegistrationStatus(
            String reference, UUID agentId
    );

    CompleteRegistrationResponse completeRegistration(
            CompleteRegistrationRequest request, UUID agentId
    );
}