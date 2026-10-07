//RegistrationApproval is the entity that enforces the requirement:
//The customer must explicitly approve the mobile number before the SIM is registered.
package com.example.sim_registration.entity;
import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

    @Entity
    @Table(
            name = "registration_approvals",
            indexes = {
                    @Index(
                            name = "idx_approval_registration",
                            columnList = "registration_id"
                    ),
                    @Index(
                            name = "idx_approval_status",
                            columnList = "status"
                    )
            }
    )
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class RegistrationApproval {

        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        private UUID id;


        //Registration that this approval belongs to,One registration has one approval record.
        @OneToOne(
                fetch = FetchType.LAZY,
                optional = false
        )
        @JoinColumn(
                name = "registration_id",
                nullable = false,
                unique = true
        )
        private SimRegistration registration;


        //Hash of the OTP.
        @Column(
                name = "otp_hash",
                nullable = false,
                length = 255
        )
        private String otpHash;


        //Number of times the customer has attempted to enter the OTP.
        @Column(nullable = false)
        private Integer otpAttempts;


        // Maximum number of OTP attempts allowed.
        @Column(nullable = false)
        private Integer maxOtpAttempts;


        //Time when the OTP becomes invalid.
        @Column(nullable = false)
        private LocalDateTime otpExpiresAt;


        //Whether the customer explicitly approved the mobile number.
        @Column(nullable = false)
        private Boolean approved;


        //Time when customer approved the registration.
        private LocalDateTime approvedAt;


        //Number of times an OTP was sent.
        @Column(nullable = false)
        private Integer otpSendCount;


        //Time when the most recent OTP was sent.
        private LocalDateTime lastOtpSentAt;


        //Current approval state.
        @Enumerated(EnumType.STRING)
        @Column(
                nullable = false,
                length = 30
        )
        private ApprovalStatus status;


        //Timestamp when the approval record was created.
        @Column(
                nullable = false,
                updatable = false
        )
        private LocalDateTime createdAt;


        //Timestamp when the approval record was last changed.
        @Column(nullable = false)
        private LocalDateTime updatedAt;


        @PrePersist
        protected void onCreate() {

            LocalDateTime now = LocalDateTime.now();

            createdAt = now;
            updatedAt = now;

            if (otpAttempts == null) {
                otpAttempts = 0;
            }

            if (maxOtpAttempts == null) {
                maxOtpAttempts = 5;
            }

            if (otpSendCount == null) {
                otpSendCount = 0;
            }

            if (approved == null) {
                approved = false;
            }

            if (status == null) {
                status = ApprovalStatus.PENDING;
            }
        }


        @PreUpdate
        protected void onUpdate() {
            updatedAt = LocalDateTime.now();
        }


}
