package com.mimaraslan.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    // http://localhost:9091/fallback/account
    @GetMapping("/account")
    public  ResponseEntity<String>  fallbackAccount() {
        return ResponseEntity.ok("Account Service: Şu anda geçici olarak hizmet verememekteyiz.");
    }


    // http://localhost:9092/fallback/notification
    @GetMapping("/notification")
    public  ResponseEntity<String>  fallbackNotification() {
        return ResponseEntity.ok("Notification Service: Şu anda geçici olarak hizmet verememekteyiz.");
    }


    // http://localhost:9094/fallback/fraud
    @GetMapping("/fraud")
    public  ResponseEntity<String>  fallbackFraud() {
        return ResponseEntity.ok("Fraud Service: Şu anda geçici olarak hizmet verememekteyiz.");
    }


    // http://localhost:9096/fallback/ledger
    @GetMapping("/ledger")
    public  ResponseEntity<String>  fallbackLedger() {
        return ResponseEntity.ok("Ledger Service: Şu anda geçici olarak hizmet verememekteyiz.");
    }
}