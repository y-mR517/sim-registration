package com.example.sim_registration.ServiceImpl;

import com.example.sim_registration.dto.request.ApproveRegistrationRequest;
import com.example.sim_registration.dto.response.ApproveRegistrationResponse;
import com.example.sim_registration.entity.*;
import com.example.sim_registration.exception.*;
import com.example.sim_registration.repository.RegistrationApprovalRepository;
import com.example.sim_registration.repository.SimRegistrationRepository;
import com.example.sim_registration.service.ApproveRegistrationService;
import com.example.sim_registration.service.AuditService;
import com.example.sim_registration.service.HashService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional
public class ApproveRegistrationServiceImpl
        implements ApproveRegistrationService {

    private final SimRegistrationRepository simRegistrationRepository;
    private final RegistrationApprovalRepository approvalRepository;
    private final HashService hashService;
    private final AuditService auditService;

    public ApproveRegistrationServiceImpl(
            SimRegistrationRepository simRegistrationRepository,RegistrationApprovalRepository approvalRepository,
            HashService hashService, AuditService auditService) {

        this.simRegistrationRepository = simRegistrationRepository;
        this.approvalRepository = approvalRepository;
        this.hashService = hashService;
        this.auditService=auditService;
    }

    @Override
    @Transactional(noRollbackFor = ApiException.class)
    public ApproveRegistrationResponse approve(
            ApproveRegistrationRequest request) {

        SimRegistration registration =
                simRegistrationRepository
                        .findByReference(request.getReference())
                        .orElseThrow(() ->{
                            log.warn("Approve failed: reason = REGISTRATION_NOT_FOUND, reference={}",
                                    request.getReference());
                            return new RegistrationNotFoundException();
                        });
        RegistrationApproval approval=
                approvalRepository
                        .findByRegistrationIdForUpdate(registration.getId())
                        .orElseThrow(() ->{
                           log.warn("Approve failed: reason=APPROVAL_RECORD_MISSING, refernce={}",
                                   registration.getReference());
                           return new InvalidOtpException();
                        });
        //Only for registration that is waiting can be approved
        if (registration.getStatus() != RegistrationStatus.PENDING || approval.getStatus() != ApprovalStatus.PENDING){
            log.warn("Approve failed: reason=INVALID_STATE, reference={}, status={}",
                    registration.getReference(), registration.getStatus());
            throw new InvalidStateException();
        }

        // Check OTP expiry
        if (approval.getOtpExpiresAt().isBefore(LocalDateTime.now())) {
            approval.setStatus(ApprovalStatus.EXPIRED);
            registration.setStatus(RegistrationStatus.EXPIRED);

            auditService.record(AuditAction.OTP_VERIFICATION_FAILED, AuditResult.FAILURE,
                    registration, "OTP expired");
            log.warn("Approve failed: reason=OTP_EXPIRED, refernce={}",registration.getReference());
            throw new OtpExpiredException();
        }

        // Compare the OTP
        String submittedHash = hashService.sha256(request.getOtp());
        boolean matches = MessageDigest.isEqual(
                submittedHash.getBytes(StandardCharsets.UTF_8),
                approval.getOtpHash().getBytes(StandardCharsets.UTF_8));

        if (!matches) {
            approval.setOtpAttempts(approval.getOtpAttempts() + 1);

            if (approval.getOtpAttempts() >= approval.getMaxOtpAttempts()) {
                approval.setStatus(ApprovalStatus.MAX_ATTEMPTS_EXCEEDED);
                registration.setStatus(RegistrationStatus.REJECTED);

                auditService.record(AuditAction.RATE_LIMIT_EXCEEDED, AuditResult.BLOCKED,
                        registration, "Max OTP attempts exceeded");

                log.warn("Approve failed: reason=MAX_ATTEMPTS_EXCEEDED, reference={}",
                        registration.getReference());
                throw new OtpAttemptsExceededException();
            }

            auditService.record(AuditAction.OTP_VERIFICATION_FAILED, AuditResult.FAILURE,
                    registration, "Incorrect OTP entered, attempt " + approval.getOtpAttempts());

            log.warn("Approve failed: reason=INVALID_OTP, reference={}, attempts={}",
                    registration.getReference(), approval.getOtpAttempts());
            throw new InvalidOtpException();
        }

        // Success
        approval.setApproved(true);
        approval.setApprovedAt(LocalDateTime.now());
        approval.setStatus(ApprovalStatus.APPROVED);
        registration.setApproved(true);
        registration.setStatus(RegistrationStatus.APPROVED);

        auditService.record(AuditAction.CUSTOMER_APPROVED, AuditResult.SUCCESS,
                registration, "Customer approved registration");

        return ApproveRegistrationResponse.builder()
                .reference(registration.getReference())
                .status(registration.getStatus())
                .approved(true)
                .message("Registration approved successfully")
                .build();
    }
}