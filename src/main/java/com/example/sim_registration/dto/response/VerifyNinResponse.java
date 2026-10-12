package com.example.sim_registration.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerifyNinResponse {

    private boolean verified;
    private String message;
}