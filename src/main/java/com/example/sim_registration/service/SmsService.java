package com.example.sim_registration.service;

import org.springframework.stereotype.Service;

@Service
public class SmsService {
    public void sendOtp(String mobileNumber, String Otp){
        System.out.println(
                "MOCK SMS: OTP " + Otp +
                        " sent to mobile number " + mobileNumber
        );
    }

    public void sendConfirmation(String mobileNumber, String message){
        System.out.println(
                "MOCK SMS: Confirmation sent to mobile number "
                + mobileNumber
                + "-"
                +message
        );
    }
}
