package com.example.sim_registration.repository;

import com.example.sim_registration.entity.RegistrationApproval;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;


public interface RegistrationApprovalRepository
        extends JpaRepository<RegistrationApproval, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE) //prevents an attacker from sending 100 guesses at the same instant
    @Query("select a from RegistrationApproval a where a.registration.id = :registrationId")

    Optional<RegistrationApproval> findByRegistrationIdForUpdate(@Param("registrationId") UUID registrationId
    );
}
