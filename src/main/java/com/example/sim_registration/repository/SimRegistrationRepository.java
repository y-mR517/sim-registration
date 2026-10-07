package com.example.sim_registration.repository;

import com.example.sim_registration.entity.RegistrationStatus;
import com.example.sim_registration.entity.SimRegistration;
import org.hibernate.sql.ast.tree.expression.Collation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface SimRegistrationRepository
        extends JpaRepository<SimRegistration, UUID> {

    Optional<SimRegistration> findByIdempotencyKey(
            String idempotencyKey
    );

    Optional<SimRegistration> findByReference(
            String reference
    );

    boolean existsByReference(
            String reference
    );

    //for duplicate SIM registrations
    boolean existsByMobileNumberHashAndStatusIn(
            String mobileNumberHash,
            Collection<RegistrationStatus> statuses
    );

    @Query("""
    SELECT COUNT(r)
    FROM SimRegistration r
    WHERE r.agent.id = :agentId
    AND r.createdAt >= :startOfDay
    AND r.createdAt < :endOfDay
""")
    long countRegistrationsForAgentToday(
            @Param("agentId") UUID agentId,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );


    long countByDeviceId(
            UUID deviceId
    );
}
