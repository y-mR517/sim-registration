package com.example.sim_registration.repository;

import com.example.sim_registration.entity.NidaMockRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NidaMockRepository extends JpaRepository<NidaMockRecord, Long> {

    Optional<NidaMockRecord> findByNin(String nin);
}
