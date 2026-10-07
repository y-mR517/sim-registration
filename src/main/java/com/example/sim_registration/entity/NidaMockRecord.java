package com.example.sim_registration.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "nida_mock_records")
public class NidaMockRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nin", nullable = false, unique = true, length = 20)
    private String nin;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "mobile_number")
    private String nidaMobileNumber;

    public NidaMockRecord() {
    }

    public NidaMockRecord(
            String nin,
            String firstName,
            String lastName,
            LocalDate dateOfBirth,
            String mobileNumber
    ) {
        this.nin = nin;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.nidaMobileNumber = mobileNumber;
    }

    public Long getId() {
        return id;
    }

    public String getNin() {
        return nin;
    }

    public void setNin(String nin) {
        this.nin = nin;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getNidaMobileNumber() {
        return nidaMobileNumber;
    }

    public void setNidaMobileNumber(String mobileNumber) {
        this.nidaMobileNumber = nidaMobileNumber;
    }
}