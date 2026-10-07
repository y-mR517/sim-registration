package com.example.sim_registration.ServiceImpl;

import com.example.sim_registration.dto.request.CompleteRegistrationRequest;
import com.example.sim_registration.dto.request.StartRegistrationRequest;
import com.example.sim_registration.dto.response.CompleteRegistrationResponse;
import com.example.sim_registration.dto.response.RegistrationStatusResponse;
import com.example.sim_registration.dto.response.StartRegistrationResponse;
import com.example.sim_registration.entity.*;
import com.example.sim_registration.exception.*;
import com.example.sim_registration.mno.MnoNumberStatus;
import com.example.sim_registration.nida.NidaClient;
import com.example.sim_registration.nida.NidaResponse;
import com.example.sim_registration.regulator.RegulatorMockService;
import com.example.sim_registration.regulator.RegulatorResponse;
import com.example.sim_registration.repository.*;
import com.example.sim_registration.security.AuthenticatedAgent;
import com.example.sim_registration.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@Transactional
public class StartRegistrationServiceImpl
        implements StartRegistrationService {

    private final HashService hashService;
    private final EncryptionService encryptionService;
    private final SimRegistrationRepository simRegistrationRepository;
    private final CustomerRepository customerRepository;
    private final AgentRepository agentRepository;
    private final DeviceInformationRepository deviceInformationRepository;
    private final NidaClient nidaClient;
    private final OtpService otpService;
    private static final Logger logger =
            LoggerFactory.getLogger(StartRegistrationServiceImpl.class);
    private final RegulatorMockService regulatorMockService;
    private final SmsService smsService;
    private final RegistrationApprovalRepository registrationApprovalRepository;
    private final AuditService auditService;
    private final MnoNumberRepository mnoNumberRepository;

    public StartRegistrationServiceImpl(
            HashService hashService,
            EncryptionService encryptionService,
            SimRegistrationRepository simRegistrationRepository,
            CustomerRepository customerRepository,
            AgentRepository agentRepository,
            DeviceInformationRepository deviceInformationRepository, NidaClient nidaClient,
            OtpService otpService,RegulatorMockService regulatorMockService,
            SmsService smsService, RegistrationApprovalRepository registrationApprovalRepository,
            AuditService auditService, MnoNumberRepository mnoNumberRepository) {

        this.hashService = hashService;
        this.encryptionService = encryptionService;
        this.simRegistrationRepository = simRegistrationRepository;
        this.customerRepository = customerRepository;
        this.agentRepository = agentRepository;
        this.deviceInformationRepository = deviceInformationRepository;
        this.nidaClient = nidaClient;
        this.otpService = otpService;
        this.regulatorMockService = regulatorMockService;
        this.smsService = smsService;
        this.registrationApprovalRepository = registrationApprovalRepository;
        this.auditService=auditService;
        this.mnoNumberRepository=mnoNumberRepository;
    }

    @Override
    public StartRegistrationResponse startRegistration(
            StartRegistrationRequest request,
            String idempotencyKey) {

        // 1. Check idempotency
        SimRegistration existing =
                simRegistrationRepository
                        .findByIdempotencyKey(idempotencyKey)
                        .orElse(null);

        if (existing != null) {

            return StartRegistrationResponse.builder()
                    .reference(existing.getReference())
                    .status(existing.getStatus().name())
                    .message("Registration already exists")
                    .createdAt(existing.getCreatedAt())
                    .build();
        }

        // 2. Find agent (from authenticated token, not the request body)
        UUID authenticatedAgentId = AuthenticatedAgent.currentAgentId();

        Agent agent = agentRepository
                .findById(authenticatedAgentId)
                .orElseThrow(() ->{
                    log.warn("Start registration failed: reason=AGENT_NOT_FOUND, agentId={}", authenticatedAgentId);
                    return new RegistrationNotFoundException();
                });

        if (agent.getStatus() != AgentStatus.ACTIVE){
            auditService.record(AuditAction.FRAUD_BLOCKED, AuditResult.BLOCKED,
                    null, "Agent not active: agentId=" + agent.getId() + ", status=" + agent.getStatus());

            log.warn("Start registration blocked: reason=AGENT_NOT_ACTIVE, agentId={}, status={}",
                    agent.getId(), agent.getStatus());

            throw new FraudBlockedException();
        }

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        long todayCount = simRegistrationRepository.countRegistrationsForAgentToday(
                agent.getId(), startOfDay, endOfDay);

        if (todayCount >= agent.getDailyRegistrationLimit()) {
            auditService.record(AuditAction.FRAUD_BLOCKED, AuditResult.BLOCKED,
                    null, "Agent daily limit reached: agentId=" + agent.getId()
                            + ", count=" + todayCount + ", limit=" + agent.getDailyRegistrationLimit());

            log.warn("Start registration blocked: reason=DAILY_LIMIT_REACHED, agentId={}, count={}, limit={}",
                    agent.getId(), todayCount, agent.getDailyRegistrationLimit());

            throw new FraudBlockedException();
        }

        //verify with Nida cusName and DOB
        NidaResponse nida =
                nidaClient.verifyNin(request.getNin());

        if (!nida.isVerified()) {
            log.warn("Identity check failed: reason = NIN_NOT_FOUND");
            throw new IdentityVerificationFailedException();
        }

        String nidaFullName =
                (nida.getFirstName() + " " + nida.getLastName()).trim();

        if (!nidaFullName.equalsIgnoreCase(request.getFullName())) {
            log.warn("Identity check failed: reason = NAME_MISMATCH");
            throw new IdentityVerificationFailedException();
        }

        LocalDate nidaDob =
                LocalDate.parse(nida.getDateOfBirth());

        if (!nidaDob.equals(request.getDateOfBirth())) {
            log.warn("Identity check failed: reason = DOB_MISMATCH");
            throw new IdentityVerificationFailedException();
        }

        // 3. Find or create customer
        String ninHash = hashService.sha256(request.getNin());

        String mobileNumberHash = hashService.sha256(request.getMobileNumber());

        boolean duplicateActive = simRegistrationRepository.existsByMobileNumberHashAndStatusIn(
                mobileNumberHash,
                java.util.List.of(
                        RegistrationStatus.PENDING,
                        RegistrationStatus.APPROVED,
                        RegistrationStatus.MNO_PENDING,
                        RegistrationStatus.MNO_REGISTERED,
                        RegistrationStatus.REGULATOR_PENDING,
                        RegistrationStatus.COMPLETED
                )
        );

        if (duplicateActive) {
            auditService.record(AuditAction.DUPLICATE_REQUEST,AuditResult.BLOCKED,
                    null, "Duplicate active registration for this mobile number");

            log.warn("Start registration blocked: reason=DUPLICATE_MOBILE_NUMBER");

            throw new DuplicateRegistrationException();
        }

        Customer customer = customerRepository
                .findByNinHash(ninHash)
                .orElseGet(() -> {

                    Customer newCustomer = new Customer();

                    newCustomer.setNinHash(ninHash);
                    newCustomer.setFullName(request.getFullName());
                    newCustomer.setDateOfBirth(request.getDateOfBirth());
                    //mobile Number already registered with NIDA
                    //This number will be used to receive OTP
                    newCustomer.setMobileNumber(nida.getNidaMobileNumber());

                    return customerRepository.save(newCustomer);
                });


        // 4. Find or create device
        String fingerprintHash =
                hashService.sha256(request.getDeviceFingerprint());

        DeviceInformation device =
                deviceInformationRepository
                        .findByDeviceFingerprintHash(fingerprintHash)
                        .orElseGet(() -> {

                            DeviceInformation newDevice =
                                    new DeviceInformation();

                            newDevice.setDeviceFingerprintHash(fingerprintHash);
                            newDevice.setImei(request.getImei());
                            newDevice.setPlatform(request.getPlatform());
                            newDevice.setTrusted(false);
                            newDevice.setCompromised(false);

                            return deviceInformationRepository.save(newDevice);
                        });

        // 5. Create registration
        SimRegistration registration = new SimRegistration();

        registration.setIdempotencyKey(idempotencyKey);
        registration.setCustomer(customer);
        registration.setAgent(agent);
        registration.setDevice(device);
        registration.setNinHash(ninHash);

        registration.setMobileNumberEncrypted(
                encryptionService.encrypt(request.getMobileNumber()));

        registration.setMobileNumberHash(
                hashService.sha256(request.getMobileNumber()));

        registration.setNetwork(request.getNetwork());

        registration.setStatus(RegistrationStatus.PENDING);

        //Generate otp
        String otp= otpService.generateOtp();

        registration.setApproved(false);

        // 6. Save
        SimRegistration saved =
                simRegistrationRepository.save(registration);

        RegistrationApproval approval = RegistrationApproval.builder()
                .registration(saved)
                .otpHash(hashService.sha256(otp))
                .otpExpiresAt(LocalDateTime.now().plusMinutes(5))
                .otpSendCount(1)
                .lastOtpSentAt(LocalDateTime.now())
                .build();

        registrationApprovalRepository.save(approval);

        //Mock SMS: Send OTP to the number registered with NIDA
        smsService.sendOtp(
                nida.getNidaMobileNumber(),
                otp
        );

        return StartRegistrationResponse.builder()
                .reference(saved.getReference())
                .status(saved.getStatus().name())
                .created(true)
                .message("OTP sent successfully. Please approve registration.")
                .createdAt(saved.getCreatedAt())
                .build();

    }

    public RegistrationStatusResponse getRegistrationStatus(
            String reference){

        SimRegistration registration =
                simRegistrationRepository
                        .findByReference(reference)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Registration not found"
                                ));

        return RegistrationStatusResponse.builder()
                .reference(registration.getReference())
                .status(registration.getStatus().name())
                .message("Registration status retrieved successfully")
                .createdAt(
                        registration.getCreatedAt().toString()
                )
                .updatedAt(
                        registration.getUpdatedAt().toString()
                )
                .completedAt(
                        registration.getCompletedAt() == null
                                ? null
                                : registration.getCompletedAt().toString()
                )
                .build();
    }

    @Override
    public CompleteRegistrationResponse completeRegistration(
            CompleteRegistrationRequest request) {

        SimRegistration registration =
                simRegistrationRepository
                        .findByReference(request.getReference())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Registration not found"
                                ));

        if (!Boolean.TRUE.equals(registration.getApproved())) {
            throw new IllegalArgumentException(
                    "Registration has not been approved."
            );
        }

        if (registration.getStatus() == RegistrationStatus.COMPLETED) {
            throw new IllegalArgumentException(
                    "Registration is already completed."
            );
        }

        // Decrypt the mobile number being registered
        String mobileNumber = encryptionService.decrypt(
                registration.getMobileNumberEncrypted()
        );

        // Look up and lock the MNO number row
        MnoNumber mnoNumber = mnoNumberRepository
                .findByPhoneNumberAndNetworkForUpdate(mobileNumber, registration.getNetwork())
                .orElseThrow(() -> {
                    auditService.record(AuditAction.MNO_FAILED, AuditResult.FAILURE,
                    registration, "Requested number not in pool: " + mobileNumber);

                    log.warn("Complete registration failed: reason=NUMBER_NOT_IN_POOL, reference={}",
                            registration.getReference());
                    return new MnoNumberUnavailableException();
                });

        if (mnoNumber.getStatus() == MnoNumberStatus.ASSIGNED) {
            auditService.record(AuditAction.MNO_FAILED, AuditResult.FAILURE,
                    registration, "Number already assigned: " + mobileNumber);

            log.warn("Complete registration failed: reason=NUMBER_ALREADY_ASSIGNED, reference={}",
                    registration.getReference());
            throw new MnoNumberUnavailableException();
        }

        mnoNumber.setStatus(MnoNumberStatus.ASSIGNED);

        auditService.record(AuditAction.MNO_SUCCESS, AuditResult.SUCCESS,
                registration, "Number Assigned: " + mobileNumber);

        // Mock MNO transaction ID
        String mnoTransactionId =
                "MNO-" + UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase();

        registration.setMnoTransactionId(mnoTransactionId);

        // Submit registration to regulator
        RegulatorResponse regulatorResponse =
                regulatorMockService.submitRegistration(
                        registration.getReference(),
                        mobileNumber
                );

        if (!regulatorResponse.isSubmitted()) {
            throw new IllegalArgumentException(
                    "Registration could not be submitted to regulator."
            );
        }

        registration.setRegulatorTransactionId(
                regulatorResponse.getTransactionId()
        );

        registration.setStatus(RegistrationStatus.COMPLETED);
        registration.setCompletedAt(LocalDateTime.now());

        smsService.sendConfirmation(
                mobileNumber,
                "Your Registration has been completed successfully."
        );

        simRegistrationRepository.save(registration);

        return CompleteRegistrationResponse.builder()
                .reference(registration.getReference())
                .status(registration.getStatus().name())
                .message("SIM registration completed successfully")
                .mnoTransactionId(mnoTransactionId)
                .build();
    }
}