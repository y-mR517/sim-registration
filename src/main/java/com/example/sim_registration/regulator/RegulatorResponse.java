package com.example.sim_registration.regulator;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegulatorResponse {

    private boolean submitted;

    private String transactionId;

    private String message;
}
