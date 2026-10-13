package com.example.sim_registration.security;

import com.example.sim_registration.entity.Agent;
import com.example.sim_registration.entity.AgentStatus;
import com.example.sim_registration.repository.AgentRepository;
import com.example.sim_registration.service.JwtService;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    @Mock private JwtService jwtService;
    @Mock private AgentRepository agentRepository;
    @Mock private FilterChain filterChain;

    @InjectMocks private JwtAuthFilter filter;

    private static final String TOKEN = "valid.test.token";

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();   // the context is shared between tests otherwise
    }

    private MockHttpServletRequest requestWithToken() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + TOKEN);
        return request;
    }

    @Test
    void filter_whenAgentIsActive_authenticatesRequest() throws Exception {
        UUID agentId = UUID.randomUUID();

        //stub
        when(jwtService.isTokenValid(TOKEN)).thenReturn(true);
        when(jwtService.extractAgentId(TOKEN)).thenReturn(agentId);
        when(agentRepository.findById(agentId)).thenReturn(Optional.of(
                Agent.builder().id(agentId).status(AgentStatus.ACTIVE).build()));

        //act
        filter.doFilter(requestWithToken(), new MockHttpServletResponse(), filterChain);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals(agentId, authentication.getPrincipal());
    }

    //  filter_whenAgentIsBlocked_doesNotAuthenticate
    @Test
    void filter_whenAgentIsBlocked_doesNotAuthenticate() throws Exception {
        UUID agentId = UUID.randomUUID();

        //Arrange: arranging a valid token that belongs to a blocked agent
        when(jwtService.isTokenValid(TOKEN)).thenReturn(true);
        when(jwtService.extractAgentId(TOKEN)).thenReturn(agentId);
        when(agentRepository.findById(agentId)).thenReturn(Optional.of(
                Agent.builder().id(agentId).status(AgentStatus.BLOCKED).build()
        ));

        //Act: run the filter (calling the method)
        filter.doFilter(requestWithToken(), new MockHttpServletResponse(), filterChain);

        //Assert: not authenticated, but the request still moves on (Spring Security rejects it next)
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        verify(filterChain).doFilter(any(),any());
    }


    // filter_whenAgentNoLongerExists_doesNotAuthenticate
    @Test
    void filter_whenAgentNoLongerExists_doesNotAuthenticate() throws Exception {
        UUID agentId = UUID.randomUUID();

        //Arrange
        when(jwtService.isTokenValid(TOKEN)).thenReturn(true);
        when(jwtService.extractAgentId(TOKEN)).thenReturn(agentId);
        when(agentRepository.findById(agentId)).thenReturn(Optional.empty());


        //Act: calling the method which actually runs the code under test
        filter.doFilter(requestWithToken(), new MockHttpServletResponse(), filterChain);

        //Assert : not authenticated, but the request still moves on
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(any(), any());
    }
}