//reaches into what JwtFilter stored for the current request, and returns it as a UUID
package com.example.sim_registration.security;

import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public class AuthenticatedAgent {

    public static UUID currentAgentId(){
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        return (UUID) principal;
    }
}
