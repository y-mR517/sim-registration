package com.example.sim_registration.nida;

import com.example.sim_registration.entity.NidaMockRecord;
import com.example.sim_registration.repository.NidaMockRepository;
import org.springframework.stereotype.Service;

@Service
public class NidaMockService {

    private final NidaMockRepository repository;

    public NidaMockService(NidaMockRepository repository){
        this.repository = repository;
    }

    public NidaResponse verifyNin(String nin) {

        return repository.findByNin(nin)
                .map(record -> new NidaResponse(
                        true,
                        record.getNin(),
                        record.getFirstName(),
                        record.getLastName(),
                        record.getDateOfBirth().toString(),
                        record.getNidaMobileNumber(),
                        "NIN verified successfully"
                ))
                .orElseGet(() ->new NidaResponse(
                        false,
                        nin,
                        null,
                        null,
                        null,
                        null,
                        "NIN not found"
                ));

    }
}