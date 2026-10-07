package com.example.sim_registration.entity;

public enum RegistrationStatus {

    // Registration has been created but customer approval is still required
    PENDING,

    // Customer has explicitly approved the mobile number
    APPROVED,

    // Request has been sent to the MNO and is awaiting a response
    MNO_PENDING,

    // MNO successfully registered the SIM
    MNO_REGISTERED,

    // Registration is waiting for submission/confirmation from the regulator
    REGULATOR_PENDING,

    // Complete end-to-end registration
    COMPLETED,

    // Registration was rejected
    REJECTED,

    // Registration was blocked because of suspected fraud
    FRAUD_BLOCKED,

    // Customer did not complete approval within the allowed time
    EXPIRED,

    // Registration failed because of a technical or integration error
    FAILED
}