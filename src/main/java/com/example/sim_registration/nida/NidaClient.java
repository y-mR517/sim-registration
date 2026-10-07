package com.example.sim_registration.nida;

import org.springframework.stereotype.Component;

@Component
public class NidaClient {

    private final NidaMockService nidaMockService;

    public NidaClient(NidaMockService nidaMockService) {
        this.nidaMockService = nidaMockService;
    }

    public NidaResponse verifyNin(String nin) {
        return nidaMockService.verifyNin(nin);
    }
}