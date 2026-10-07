package com.example.sim_registration.controller;

import com.example.sim_registration.mno.MnoMockService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mno")
public class MnoController {

    private final MnoMockService mnoMockService;

    public MnoController(MnoMockService mnoMockService) {
        this.mnoMockService = mnoMockService;
    }

    @GetMapping("/available-numbers/{network}")
    public List<String> getAvailableNumbers(
            @PathVariable String network) {

        return mnoMockService.getAvailableNumbers(network);
    }
}

