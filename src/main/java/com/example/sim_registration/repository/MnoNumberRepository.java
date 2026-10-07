package com.example.sim_registration.repository;

import com.example.sim_registration.entity.MnoNumber;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MnoNumberRepository extends JpaRepository<MnoNumber, Long> {

    List<MnoNumber> findByNetwork(String network);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from MnoNumber m where m.phoneNumber = :phoneNumber and m.network = :network")
    Optional<MnoNumber>findByPhoneNumberAndNetworkForUpdate(
            @Param("phoneNumber") String phoneNumber,
            @Param("network") String network
    );
}