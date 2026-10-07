package com.example.sim_registration.dto.response;

import com.example.sim_registration.entity.RegistrationStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApproveRegistrationResponse {

    private String reference;

    private RegistrationStatus status;

    private String message;

    private Boolean approved;
}