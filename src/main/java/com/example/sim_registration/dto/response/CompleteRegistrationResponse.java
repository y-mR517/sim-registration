package com.example.sim_registration.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompleteRegistrationResponse {

    private String reference;

    private String status;

    private String message;

    private String mnoTransactionId;
}
