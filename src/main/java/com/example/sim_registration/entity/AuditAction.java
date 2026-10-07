package com.example.sim_registration.entity;

public enum AuditAction {

    // NIDA verification
    NIN_VERIFICATION_STARTED,
    NIN_VERIFIED,
    NIN_VERIFICATION_FAILED,

    // Registration lifecycle
    REGISTRATION_STARTED,
    REGISTRATION_REJECTED,

    // Customer approval / OTP
    OTP_SENT,
    OTP_VERIFICATION_FAILED,
    OTP_VERIFIED,
    CUSTOMER_APPROVED,

    // MNO integration
    MNO_REQUESTED,
    MNO_SUCCESS,
    MNO_FAILED,

    // Regulator integration
    REGULATOR_SUBMITTED,
    REGULATOR_SUCCESS,
    REGULATOR_FAILED,

    // Fraud and security
    FRAUD_CHECK_STARTED,
    FRAUD_BLOCKED,
    RATE_LIMIT_EXCEEDED,
    DUPLICATE_REQUEST,

    // Final state
    REGISTRATION_COMPLETED
}