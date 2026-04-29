package com.mimaraslan.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    /**
     * Circuit breaker fallback must accept the same HTTP method as the original request.
     * Previously only GET was mapped, so POST /account/register produced 405 METHOD_NOT_ALLOWED.
     */
    @RequestMapping(
            value = "/account",
            method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE}
    )
    public ResponseEntity<String> fallbackAccount() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Account Service: Şu anda geçici olarak hizmet verememekteyiz.");
    }

    @RequestMapping(
            value = "/ledger",
            method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE}
    )
    public ResponseEntity<String> fallbackLedger() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Ledger Service: Şu anda geçici olarak hizmet verememekteyiz.");
    }

    @RequestMapping(
            value = "/fraud",
            method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE}
    )
    public ResponseEntity<String> fallbackFraud() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Fraud Service: Şu anda geçici olarak hizmet verememekteyiz.");
    }

    @RequestMapping(
            value = "/notification",
            method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE}
    )
    public ResponseEntity<String> fallbackNotification() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Notification Service: Şu anda geçici olarak hizmet verememekteyiz.");
    }
}