package com.example.sim_registration.entity;

public enum AgentStatus {

    // Agent is allowed to perform registrations
    ACTIVE,

    // Temporarily restricted from performing registrations
    SUSPENDED,

    // Permanently blocked because of serious security/fraud concerns
    BLOCKED,

    // Agent account is no longer active
    INACTIVE
}