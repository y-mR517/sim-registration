package com.example.sim_registration.entity;

import com.example.sim_registration.mno.MnoNumberStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "mno_numbers")
public class MnoNumber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String network;

    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MnoNumberStatus status;

    public MnoNumber() {
    }

    public MnoNumber(String network, String phoneNumber, MnoNumberStatus status) {
        this.network = network;
        this.phoneNumber = phoneNumber;
        this.status = status;
    }

    @PrePersist
    protected void onCreate(){
        if (status == null){
            status = MnoNumberStatus.AVAILABLE;
        }
    }
}