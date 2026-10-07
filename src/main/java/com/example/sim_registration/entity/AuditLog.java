package com.example.sim_registration.entity;

import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "audit_logs",
        indexes = {
                @Index(
                        name = "idx_audit_registration",
                        columnList = "registration_id"
                ),
                @Index(
                        name = "idx_audit_agent",
                        columnList = "agent_id"
                ),
                @Index(
                        name = "idx_audit_action",
                        columnList = "action"
                ),
                @Index(
                        name = "idx_audit_created_at",
                        columnList = "created_at"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    /*
     Registration associated with this event.
     Nullable because some security events may happen
     before a registration has been created.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registration_id")
    private SimRegistration registration;


    //Agent responsible for the action.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id")
    private Agent agent;


    //What happened?
    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 50
    )
    private AuditAction action;


    //Whether the action succeeded or failed.
    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 20
    )
    private AuditResult result;


    //Human-readable description of the event.
    @Column(length = 1000)
    private String description;


    /*
     Hash of the IP address associated with the request,
     Useful for fraud investigation without unnecessarily storing raw IP addresses.
     */
    @Column(
            name = "ip_hash",
            length = 64
    )
    private String ipHash;


    //Device associated with the action.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id")
    private DeviceInformation device;


    /*
     Correlation ID used to trace a request across services and logs.
     eg:9b2c1c1e-...
     */
    @Column(
            name = "correlation_id",
            length = 100
    )
    private String correlationId;


    //External transaction/reference ID.
    @Column(
            name = "external_reference",
            length = 150
    )
    private String externalReference;


    //Timestamp of the event.
    @Column(
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
