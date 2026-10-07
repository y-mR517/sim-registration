package com.example.sim_registration.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrationStatusResponse {

    private String reference;

    private String status;

    private String message;

    private String createdAt;

    private String updatedAt;

    private String completedAt;
}
