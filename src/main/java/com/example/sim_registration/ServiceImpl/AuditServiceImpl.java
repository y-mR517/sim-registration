package com.example.sim_registration.ServiceImpl;

import com.example.sim_registration.entity.AuditAction;
import com.example.sim_registration.entity.AuditLog;
import com.example.sim_registration.entity.AuditResult;
import com.example.sim_registration.entity.SimRegistration;
import com.example.sim_registration.repository.AuditLogRepository;
import com.example.sim_registration.service.AuditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditServiceImpl(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(
            AuditAction action,
            AuditResult result,
            SimRegistration registration,
            String description) {

        AuditLog log = AuditLog.builder()
                .action(action)
                .result(result)
                .registration(registration)
                .description(description)
                .build();

        auditLogRepository.save(log);
    }
}