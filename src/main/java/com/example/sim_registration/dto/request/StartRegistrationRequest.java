package com.example.sim_registration.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StartRegistrationRequest {

    @NotBlank(message = "NIN is required")
    private String nin;

    @NotBlank(message = "Full name is required")
    @Size(max = 200, message = "Full name is too long")
    private String fullName;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Mobile number is required")
    @Pattern(
            regexp = "^255[67][0-9]{8}$",
            message = "Invalid Tanzanian mobile number"
    )
    private String mobileNumber;

    @NotBlank(message = "Network name is required")
    private String network;

    @NotBlank(message = "Device fingerprint is required")
    private String deviceFingerprint;

    @NotBlank(message = "IMEI is required")
    private String imei;

    @NotBlank(message = "Platform is required")
    private String platform;
}