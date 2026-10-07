package com.example.sim_registration.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerifyNinResponse {

    private boolean verified;
    private String nin;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String message;
}