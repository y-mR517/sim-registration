package com.example.sim_registration.entity;

public enum ApprovalStatus {

    // Customer has not yet approved the mobile number
    PENDING,

    // Customer successfully verified the OTP and approved
    APPROVED,

    // Customer explicitly rejected the registration
    REJECTED,

    // OTP/approval window expired
    EXPIRED,

    // Too many incorrect OTP attempts
    MAX_ATTEMPTS_EXCEEDED
}