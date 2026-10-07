package com.example.sim_registration.entity;

import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "agents",
        indexes = {
                @Index(name = "idx_agent_code", columnList = "agent_code"),
                @Index(name = "idx_agent_username", columnList = "username")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Agent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    //Unique identification/reference for the agent. Example: AGT-8F31A2BC
    @Column(
            name = "agent_code",
            nullable = false,
            unique = true,
            length = 50
    )
    private String agentCode;

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 100)
    private String lastName;

    //Username used for authentication.

    @Column(
            nullable = false,
            unique = true,
            length = 100
    )
    private String username;

    /*
     IMPORTANT:
     This stores a BCrypt/Argon2 hash,
     NOT the actual password.
     */
    @Column(
            name = "password_hash",
            nullable = false,
            length = 255
    )
    private String passwordHash;

    @Column(
            unique = true,
            length = 20
    )
    private String phoneNumber;

    @Column(
            unique = true,
            length = 150
    )
    private String email;

    @Column(length = 255)
    private String location;

    //Controls whether the agent can perform registrations.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AgentStatus status;

    /*
     Maximum number of registrations
     an agent is allowed to perform per day.
     */
    @Column(nullable = false)
    private Integer dailyRegistrationLimit;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;


    //Runs automatically before a new Agent is inserted into the database.
    @PrePersist
    protected void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;

        //Generate agent code if one was not supplied.
        if (agentCode == null || agentCode.isBlank()) {

            agentCode = "AGT-" +
                    UUID.randomUUID()
                            .toString()
                            .substring(0, 8)
                            .toUpperCase();
        }

        //New agents are active by default.
        if (status == null) {
            status = AgentStatus.ACTIVE;
        }

        // Default daily registration limit.
        if (dailyRegistrationLimit == null) { //for fraud-prevention requirement
            dailyRegistrationLimit = 100;
        }
    }


    // Runs automatically before an existing Agent is updated
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
