package com.example.sim_registration.nida;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NidaResponse {

    private boolean verified;
    private String nin;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String nidaMobileNumber;
    private String message;

}