package com.example.sim_registration.nida;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mock/nida")
public class NidaMockController {

    private final NidaMockService nidaMockService;

    public NidaMockController(NidaMockService nidaMockService) {
        this.nidaMockService = nidaMockService;
    }

    @GetMapping("/verify/{nin}")
    public ResponseEntity<NidaResponse> verifyNin(
            @PathVariable String nin
    ) {
        return ResponseEntity.ok(
                nidaMockService.verifyNin(nin)
        );
    }
}