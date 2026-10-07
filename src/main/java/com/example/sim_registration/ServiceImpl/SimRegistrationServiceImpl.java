package com.example.sim_registration.ServiceImpl;

import com.example.sim_registration.dto.request.StartRegistrationRequest;
import com.example.sim_registration.dto.response.RegistrationResult;
import com.example.sim_registration.dto.response.StartRegistrationResponse;
import com.example.sim_registration.entity.SimRegistration;
import com.example.sim_registration.repository.SimRegistrationRepository;
import com.example.sim_registration.service.SimRegistrationService;
import com.example.sim_registration.service.StartRegistrationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SimRegistrationServiceImpl
        implements SimRegistrationService {

    private final StartRegistrationService startRegistrationService;
    private final SimRegistrationRepository simRegistrationRepository;

    public SimRegistrationServiceImpl(
            StartRegistrationService startRegistrationService,
            SimRegistrationRepository simRegistrationRepository) {

        this.startRegistrationService = startRegistrationService;
        this.simRegistrationRepository = simRegistrationRepository;
    }

    @Override
    public SimRegistration findByIdempotencyKey(String idempotencyKey) {

        return simRegistrationRepository
                .findByIdempotencyKey(idempotencyKey)
                .orElse(null);
    }

    @Override
    public StartRegistrationResponse saveRegistration(
            StartRegistrationRequest request,
            String idempotencyKey) {

        return startRegistrationService.startRegistration(
                request,
                idempotencyKey
        );
    }
}