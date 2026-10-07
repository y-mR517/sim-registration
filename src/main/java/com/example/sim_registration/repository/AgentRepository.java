package com.example.sim_registration.repository;

import com.example.sim_registration.entity.Agent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AgentRepository extends JpaRepository<Agent, UUID> {

    Optional<Agent> findByUsername(String username);

    Optional<Agent> findByAgentCode(String agentCode);

    boolean existsByUsername(String username);

    boolean existsByAgentCode(String agentCode);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);
}
