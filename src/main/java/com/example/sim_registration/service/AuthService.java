package com.example.sim_registration.service;

import com.example.sim_registration.dto.request.LoginRequest;
import com.example.sim_registration.dto.response.LoginResponse;
import com.example.sim_registration.entity.Agent;
import com.example.sim_registration.entity.AgentStatus;
import com.example.sim_registration.exception.InvalidCredentialsException;
import com.example.sim_registration.repository.AgentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthService {

    private final AgentRepository agentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(AgentRepository agentRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {

        this.agentRepository = agentRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request){

        Agent agent = agentRepository
                .findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.warn("Login failed: reason=USERNAME_NOT_FOUND, username={}", request.getUsername());
                    return new InvalidCredentialsException();
                });

        //a bcrypt password, never decrypt the hash - its one way
        if (!passwordEncoder.matches(request.getPassword(), agent.getPasswordHash())){
            log.warn("Login failed: reason=WRONG_PASSWORD, agentId={}", agent.getId());
            throw new InvalidCredentialsException();
        }

        //a single invalid credentials covers both wrong password and
        // right password but blocked(difference from startRegistration)
        if (agent.getStatus() != AgentStatus.ACTIVE){
            log.warn("Login failed: reason=AGENT_NOT_ACTIVE, agentId={}, status={}", agent.getId(), agent.getStatus());
            throw new InvalidCredentialsException();
        }

        String token = jwtService.generateToken(agent.getId(), agent.getUsername());

        log.info("Login successful: agentId={}", agent.getId());

        return LoginResponse.builder()
                .token(token)
                .agentCode(agent.getAgentCode())
                .build();
    }
}
