package com.example.sim_registration.service;

import com.example.sim_registration.dto.request.ApproveRegistrationRequest;
import com.example.sim_registration.dto.response.ApproveRegistrationResponse;

public interface ApproveRegistrationService {
    ApproveRegistrationResponse approve(
            ApproveRegistrationRequest request
    );
}
