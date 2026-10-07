package com.example.sim_registration.regulator;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RegulatorMockService {

    public RegulatorResponse submitRegistration(String reference, String mobileNumber){
        String transactionId =
                "REGULATOR-" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0,8)
                                .toUpperCase();

        return RegulatorResponse.builder()
                .submitted(true)
                .transactionId(transactionId)
                .message("Registration submitted successfully")
                .build();
    }

}
