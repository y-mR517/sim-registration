package com.example.sim_registration.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "customers",
        indexes = {
                @Index(name = "idx_customer_nin_hash", columnList = "nin_hash"),
                @Index(name = "idx_customer_mobile_number", columnList = "mobile_number")
        }
)
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "nin_hash",
            nullable = false,
            unique = true,
            length = 64
    )
    private String ninHash;

    @Column(nullable = false, length = 200)
    private String fullName;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Column(
            name = "mobile_number",
            nullable = false,
            length = 20
    )
    private String mobileNumber;

    @Column(
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Required by JPA
    public Customer() {
    }

    public Long getId() {
        return id;
    }

    public String getNinHash() {
        return ninHash;
    }

    public void setNinHash(String ninHash) {
        this.ninHash = ninHash;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}