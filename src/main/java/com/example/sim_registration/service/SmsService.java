package com.example.sim_registration.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SmsService {

    public void sendOtp(String mobileNumber, String otp) {
        // The OTP is deliberately never logged. Nothing is really sent: this is a simulation.
        log.info("MOCK SMS (simulated, nothing delivered): OTP message for {}", mask(mobileNumber));
    }

    public void sendConfirmation(String mobileNumber, String message) {
        // The message text is not logged either, in case it ever contains customer details.
        log.info("MOCK SMS (simulated, nothing delivered): confirmation for {}", mask(mobileNumber));
    }

    // 255712345678 -> 25571****78: enough to tell requests apart, not enough to identify a person
    private String mask(String number) {
        if (number == null || number.length() < 8) {
            return "****";
        }
        return number.substring(0, 5) + "****" + number.substring(number.length() - 2);
    }
}