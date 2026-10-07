package com.example.sim_registration.entity;

import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "device_information",
        indexes = {
                @Index(
                        name = "idx_device_fingerprint",
                        columnList = "device_fingerprint_hash"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Unique device fingerprint (hashed)
    @Column(name = "device_fingerprint_hash", nullable = false, length = 64)
    private String deviceFingerprintHash;

    // Primary IMEI
    @Column(nullable = false, length = 50)
    private String imei;

    // Android / iOS
    @Column(nullable =false, length = 20)
    private String platform;

    // Samsung, Apple, Xiaomi...
    @Column(length = 100)
    private String manufacturer;

    // Galaxy S25, iPhone 16...
    @Column(length = 100)
    private String model;

    // OS version
    @Column(length = 50)
    private String osVersion;

    // Version of your registration app
    @Column(length = 30)
    private String appVersion;

    // Fraud detection
    @Column
    private Integer riskScore;

    @Column(nullable = false)
    private Boolean trusted;

    @Column(nullable = false)
    private Boolean compromised;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;

        if (riskScore == null)
            riskScore = 0;

        if (trusted == null)
            trusted = false;

        if (compromised == null)
            compromised = false;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}