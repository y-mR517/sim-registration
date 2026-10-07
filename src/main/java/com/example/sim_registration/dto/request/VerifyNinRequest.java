package com.example.sim_registration.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerifyNinRequest {

    @NotBlank(message = "NIN is required")
    private String nin;
}