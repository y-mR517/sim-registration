package com.example.sim_registration.mno;

import com.example.sim_registration.entity.MnoNumber;
import com.example.sim_registration.repository.MnoNumberRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MnoMockService {

    private final MnoNumberRepository repository;

    public MnoMockService(MnoNumberRepository repository) {
        this.repository = repository;
    }

    public List<String> getAvailableNumbers(String network) {

        return repository.findByNetwork(network.toUpperCase())
                .stream()
                .map(MnoNumber::getPhoneNumber)
                .toList();
    }
}