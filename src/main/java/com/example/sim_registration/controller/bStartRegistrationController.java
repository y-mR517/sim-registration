package com.example.sim_registration.controller;

import com.example.sim_registration.dto.request.CompleteRegistrationRequest;
import com.example.sim_registration.dto.request.StartRegistrationRequest;
import com.example.sim_registration.dto.response.CompleteRegistrationResponse;
import com.example.sim_registration.dto.response.RegistrationResult;
import com.example.sim_registration.dto.response.RegistrationStatusResponse;
import com.example.sim_registration.dto.response.StartRegistrationResponse;
import com.example.sim_registration.entity.RegistrationStatus;
import com.example.sim_registration.service.StartRegistrationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class bStartRegistrationController {

    private final StartRegistrationService startRegistrationService;

    public bStartRegistrationController(
            StartRegistrationService startRegistrationService) {

        this.startRegistrationService = startRegistrationService;
    }

    @PostMapping("/start-registration")
    public ResponseEntity<StartRegistrationResponse> startRegistration(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody StartRegistrationRequest request) {


        StartRegistrationResponse result =
                startRegistrationService.startRegistration(
                        request,
                        idempotencyKey
                );

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/registrations/{reference}/status")
    public ResponseEntity<RegistrationStatusResponse> getRegistrationStatus(
            @PathVariable String reference) {

        RegistrationStatusResponse response =
                startRegistrationService.getRegistrationStatus(reference);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/registrations/complete")
    public ResponseEntity<CompleteRegistrationResponse> completeRegistration(

            @Valid
            @RequestBody
            CompleteRegistrationRequest request) {

        CompleteRegistrationResponse response =
                startRegistrationService.completeRegistration(request);

        return ResponseEntity.ok(response);
    }

}