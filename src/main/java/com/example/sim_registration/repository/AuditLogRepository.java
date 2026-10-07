package com.example.sim_registration.repository;

import com.example.sim_registration.entity.AuditLog;
import com.example.sim_registration.entity.AuditAction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AuditLogRepository
        extends JpaRepository<AuditLog, UUID> {

    List<AuditLog> findByRegistrationIdOrderByCreatedAtDesc(
            UUID registrationId
    );

    List<AuditLog> findByAgentIdOrderByCreatedAtDesc(
            UUID agentId
    );

    long countByAgentIdAndAction(
            UUID agentId,
            AuditAction action
    );
}
