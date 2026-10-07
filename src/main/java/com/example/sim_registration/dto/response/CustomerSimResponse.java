package com.example.sim_registration.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerSimResponse {

    private String reference;

    private String mobileNumber;

    private String status;

    private String registeredAt;
}
