package com.example.sim_registration.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "sim_registrations",
        indexes = {
                @Index(name = "idx_registration_reference", columnList = "reference"),
                @Index(name = "idx_registration_status", columnList = "status"),
                @Index(name = "idx_registration_customer", columnList = "customer_id"),
                @Index(name = "idx_registration_agent", columnList = "agent_id"),
                @Index(name = "idx_registration_device", columnList = "device_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(
            nullable = false,
            unique = true,
            length = 50
    )
    private String reference;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "customer_id",
            nullable = false
    )
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "agent_id",
            nullable = false
    )
    private Agent agent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "device_id"
    )
    private DeviceInformation device;

    @Column(name = "nin_hash", nullable = false, length = 64)
    private String ninHash;

    @Column(length = 30)
    private String network;

    /*
     * Hash used for secure lookup/comparison of the mobile number.
     */
    @Column(
            name = "mobile_number_hash",
            nullable = false,
            length = 64
    )
    private String mobileNumberHash;

    /*
     * Encrypted mobile number.
     * Encryption/decryption will be handled by the service layer.
     */
    @Column(
            name = "mobile_number_encrypted",
            nullable = false,
            length = 500
    )
    private String mobileNumberEncrypted;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 40
    )
    private RegistrationStatus status;

    @Column(
            name = "nida_transaction_id",
            length = 100
    )
    private String nidaTransactionId;

    @Column(
            name = "mno_transaction_id",
            length = 100
    )
    private String mnoTransactionId;

    @Column(
            name = "regulator_transaction_id",
            length = 100
    )
    private String regulatorTransactionId;

    /*
     * Prevents the same registration request from
     * being processed more than once.
     */
    @Column(
            name = "idempotency_key",
            unique = true,
            length = 100
    )
    private String idempotencyKey;

    @Column( name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column( name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;

    private LocalDateTime completedAt;

    @Column(name = "otp_hash", length = 64)
    private String otpHash;

    private LocalDateTime otpExpiry;

    private Boolean approved = false;


    @PrePersist
    protected void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;

        if (reference == null || reference.isBlank()) {
            reference = "REG-" +
                    UUID.randomUUID()
                            .toString()
                            .substring(0, 8)
                            .toUpperCase();
        }

        if (status == null) {
            status = RegistrationStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}