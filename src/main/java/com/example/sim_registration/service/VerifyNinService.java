package com.example.sim_registration.service;

import com.example.sim_registration.dto.request.VerifyNinRequest;
import com.example.sim_registration.dto.response.VerifyNinResponse;
import com.example.sim_registration.exception.IdentityVerificationFailedException;
import com.example.sim_registration.nida.NidaClient;
import com.example.sim_registration.nida.NidaResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
public class VerifyNinService {

    private final NidaClient nidaClient;

    public VerifyNinService(NidaClient nidaClient) {
        this.nidaClient = nidaClient;
    }

    public VerifyNinResponse verify(VerifyNinRequest request) {

        NidaResponse nida = nidaClient.verifyNin(request.getNin());

        if (!nida.isVerified()) {
            log.warn("Identity check failed: reason = NIN_NOT_FOUND");
            throw new IdentityVerificationFailedException();
        }

        return VerifyNinResponse.builder()
                .verified(true)
                .firstName(nida.getFirstName())
                .lastName(nida.getLastName())
                .dateOfBirth(nida.getDateOfBirth())
                .message("NIN verified successfully")
                .build();
    }
}