package com.example.sim_registration.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterSimRequest {

    @NotBlank(message = "Registration reference is required")
    private String reference;

    //Idempotency key prevents duplicate requests.
    @NotBlank(message = "Idempotency key is required")
    private String idempotencyKey;

    //SIM serial/ICCID.
    @NotBlank(message = "SIM serial number is required")
    private String simSerialNumber;
}
