package com.example.sim_registration.controller;

import com.example.sim_registration.dto.request.VerifyNinRequest;
import com.example.sim_registration.dto.response.VerifyNinResponse;
import com.example.sim_registration.service.VerifyNinService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class aVerifyNinController {

    private final VerifyNinService verifyNinService;

    public aVerifyNinController(VerifyNinService verifyNinService) {
        this.verifyNinService = verifyNinService;
    }

    @PostMapping("/verify-nin")
    public ResponseEntity<VerifyNinResponse> verifyNin(
            @Valid @RequestBody VerifyNinRequest request) {

        return ResponseEntity.ok(
                verifyNinService.verify(request)
        );
    }
}