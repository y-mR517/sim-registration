//this filter does not reject invalid tokens itself
package com.example.sim_registration.security;

import com.example.sim_registration.entity.AgentStatus;
import com.example.sim_registration.repository.AgentRepository;
import com.example.sim_registration.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final AgentRepository agentRepository;

    public JwtAuthFilter(JwtService jwtService, AgentRepository agentRepository) {
        this.jwtService = jwtService;
        this.agentRepository = agentRepository;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String header =request.getHeader("Authorization"); //standard place a client sends a token

        if (header != null && header.startsWith("Bearer ")){ //checks the format
            String token = header.substring(7); //stips off the word "Bearer " leaving only thr token

            //isTokenValid is a safe wrapper around parseClaims
            // it turns throws an exception int returns true of false
            // checking if the signature does match or is expired
            if (jwtService.isTokenValid(token)){
                UUID agentId = jwtService.extractAgentId(token);
                boolean agentIsActive = agentRepository.findById(agentId)
                        .map(agent -> agent.getStatus() == AgentStatus.ACTIVE)
                        .orElse(false);

                if (agentIsActive) {
                    var authentication = new UsernamePasswordAuthenticationToken(
                            agentId, null, List.of());
                    //actual login for this one request
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    log.warn("Token rejected: reason=AGENT_NOT_ACTIVE_OR_MISSING, agentId={}", agentId);
                }
            }
        }

        //called at the end regardless ,
        // lets the request continue to the next step (eventually controller)
        filterChain.doFilter(request, response);
    }
}
