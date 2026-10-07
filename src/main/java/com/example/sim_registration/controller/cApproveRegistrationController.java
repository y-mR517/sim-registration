package com.example.sim_registration.controller;

import com.example.sim_registration.dto.request.ApproveRegistrationRequest;
import com.example.sim_registration.dto.response.ApproveRegistrationResponse;
import com.example.sim_registration.service.ApproveRegistrationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class cApproveRegistrationController {

    private final ApproveRegistrationService approveRegistrationService;

    public cApproveRegistrationController(
            ApproveRegistrationService approveRegistrationService) {
        this.approveRegistrationService = approveRegistrationService;
    }

    @PostMapping("/approve-registration")
    public ResponseEntity<ApproveRegistrationResponse> approveRegistration(
            @Valid @RequestBody ApproveRegistrationRequest request) {

        ApproveRegistrationResponse response =
                approveRegistrationService.approve(request);

        return ResponseEntity.ok(response);
    }
}