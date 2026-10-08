package com.example.sim_registration.ServiceImpl;

import com.example.sim_registration.dto.request.ApproveRegistrationRequest;
import com.example.sim_registration.entity.Agent;
import com.example.sim_registration.entity.AuditAction;
import com.example.sim_registration.entity.AuditResult;
import com.example.sim_registration.entity.RegistrationStatus;
import com.example.sim_registration.entity.SimRegistration;
import com.example.sim_registration.exception.RegistrationNotFoundException;
import com.example.sim_registration.repository.RegistrationApprovalRepository;
import com.example.sim_registration.repository.SimRegistrationRepository;
import com.example.sim_registration.service.AuditService;
import com.example.sim_registration.service.HashService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApproveRegistrationServiceImplTest {

    @Mock private SimRegistrationRepository simRegistrationRepository;
    @Mock private RegistrationApprovalRepository approvalRepository;
    @Mock private HashService hashService;
    @Mock private AuditService auditService;

    @InjectMocks private ApproveRegistrationServiceImpl service;

    private static final String REFERENCE = "REG-TEST-0001";

    @Test
    void approve_whenAgentDoesNotOwnRegistration_throwsNotFoundAndChangesNothing() {
        // Arrange: a registration owned by one agent, approval attempted by another
        UUID ownerId = UUID.randomUUID();
        UUID otherAgentId = UUID.randomUUID();

        Agent owner = Agent.builder().id(ownerId).build();
        SimRegistration registration = SimRegistration.builder()
                .id(UUID.randomUUID())
                .reference(REFERENCE)
                .agent(owner)
                .status(RegistrationStatus.PENDING)
                .build();

        when(simRegistrationRepository.findByReference(REFERENCE))
                .thenReturn(Optional.of(registration));

        ApproveRegistrationRequest request = new ApproveRegistrationRequest(REFERENCE, "123456");

        // Act + Assert: the request is rejected
        assertThrows(RegistrationNotFoundException.class,
                () -> service.approve(request, otherAgentId));

        // Assert: nothing was approved
        assertEquals(RegistrationStatus.PENDING, registration.getStatus());

        // Assert: the OTP was never looked at, so no guess could be counted or checked
        verify(approvalRepository, never()).findByRegistrationIdForUpdate(any());
        verifyNoInteractions(hashService);

        // Assert: the attempt was recorded for investigation
        verify(auditService).record(eq(AuditAction.FRAUD_BLOCKED), eq(AuditResult.BLOCKED),
                eq(registration), contains("does not own"));
    }
}