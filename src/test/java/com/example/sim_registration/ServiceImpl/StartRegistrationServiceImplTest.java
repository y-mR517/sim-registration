package com.example.sim_registration.ServiceImpl;

import com.example.sim_registration.dto.request.CompleteRegistrationRequest;
import com.example.sim_registration.entity.Agent;
import com.example.sim_registration.entity.AuditAction;
import com.example.sim_registration.entity.AuditResult;
import com.example.sim_registration.entity.RegistrationStatus;
import com.example.sim_registration.entity.SimRegistration;
import com.example.sim_registration.exception.RegistrationNotFoundException;
import com.example.sim_registration.regulator.RegulatorMockService;
import com.example.sim_registration.repository.MnoNumberRepository;
import com.example.sim_registration.repository.SimRegistrationRepository;
import com.example.sim_registration.service.AuditService;
import com.example.sim_registration.service.SmsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StartRegistrationServiceImplTest {

    @Mock private SimRegistrationRepository simRegistrationRepository;
    @Mock private AuditService auditService;
    @Mock private MnoNumberRepository mnoNumberRepository;
    @Mock private RegulatorMockService regulatorMockService;
    @Mock private SmsService smsService;

    @InjectMocks private StartRegistrationServiceImpl service;

    private static final String REFERENCE = "REG-TEST-0001";

    @Test
    void completeRegistration_whenAgentDoesNotOwnRegistration_throwsNotFoundAndChangesNothing() {
        // Arrange: a registration owned by one agent, approval attempted by another
        UUID ownerId = UUID.randomUUID();
        UUID otherAgentId = UUID.randomUUID();

        Agent owner = Agent.builder().id(ownerId).build();
        SimRegistration registration = SimRegistration.builder()
                .id(UUID.randomUUID())
                .reference(REFERENCE)
                .agent(owner)
                .status(RegistrationStatus.APPROVED)
                .approved(true)
                .build();

        when(simRegistrationRepository.findByReference(REFERENCE))
                .thenReturn(Optional.of(registration));

        CompleteRegistrationRequest request = new CompleteRegistrationRequest(REFERENCE);

        // Act + Assert: the request is rejected
        assertThrows(RegistrationNotFoundException.class,
                () -> service.completeRegistration(request, otherAgentId));

        // Assert: the registration didn't become completed
        assertEquals(RegistrationStatus.APPROVED, registration.getStatus());

        // Assert: no MNO number was assigned, nothing was sent to the regulator, no SMS went out
        verifyNoInteractions(mnoNumberRepository,regulatorMockService, smsService);

        // Assert: the attempt was recorded for investigation
        verify(auditService).record(eq(AuditAction.FRAUD_BLOCKED), eq(AuditResult.BLOCKED),
                eq(registration), contains("does not own"));
    }

    @Test
    void completeRegistration_whenRegistrationReferenceDoesNotExist_throwsNotFoundAndChangesNothing() {

        UUID anyAgentId = UUID.randomUUID();

        when(simRegistrationRepository.findByReference(REFERENCE))
                .thenReturn(Optional.empty());

        CompleteRegistrationRequest request = new CompleteRegistrationRequest(REFERENCE);

        // Act + Assert: the request is rejected
        assertThrows(RegistrationNotFoundException.class,
                () -> service.completeRegistration(request, anyAgentId));

        //Assert: nothing downstream was touched
        verifyNoInteractions(mnoNumberRepository, regulatorMockService, smsService);
    }

    @Test
    void getRegistrationStatus_whenAgentDoesNotOwnRegistration_throwsNotFoundAndChangesNothing() {
        //a registration owned by one agent, status requested by another
        UUID ownerId = UUID.randomUUID();
        UUID otherAgentId = UUID.randomUUID();

        Agent owner = Agent.builder().id(ownerId).build();
        SimRegistration registration = SimRegistration.builder()
                        .id(UUID.randomUUID())
                                .reference(REFERENCE)
                                        .agent(owner)
                                                .status(RegistrationStatus.APPROVED)
                                                        .build();

        when(simRegistrationRepository.findByReference(REFERENCE))
                .thenReturn(Optional.of(registration));


        // Act + Assert: the request is rejected
        assertThrows(RegistrationNotFoundException.class,
                () -> service.getRegistrationStatus(REFERENCE, otherAgentId));

        //Assert: the attempt was recorded for investigation
        verify(auditService).record(eq(AuditAction.FRAUD_BLOCKED), eq(AuditResult.BLOCKED),
                eq(registration), contains("does not own"));
    }
}