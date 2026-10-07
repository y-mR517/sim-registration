package com.example.sim_registration.service;

import com.example.sim_registration.entity.AuditAction;
import com.example.sim_registration.entity.AuditResult;
import com.example.sim_registration.entity.SimRegistration;

public interface AuditService {

    void record(
            AuditAction action,
            AuditResult result,
            SimRegistration registration,
            String description
    );
}
