package com.example.sim_registration.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class StartRegistrationResponse {

    private String reference;

    private String status;

    private boolean created;

    private String message;

    private LocalDateTime createdAt;



}
