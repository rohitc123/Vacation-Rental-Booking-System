package com.example.AirBnb.App.controller;

import com.example.AirBnb.App.service.BookingService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class StripeWebhookController {

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    private final BookingService bookingService;

    //Event is listened by this controller

    @PostMapping("/payment")
    public ResponseEntity<Void> handleStripeEvent(
            @RequestBody String payload, // RAW body is mandatory
            @RequestHeader("Stripe-Signature") String sigHeader
    ){
        try {
            // varify signature(security)
            Event event= Webhook.constructEvent(payload, sigHeader, endpointSecret);
            bookingService.capturePayment(event);
            return ResponseEntity.noContent().build();
        } catch (SignatureVerificationException e) {
            throw new RuntimeException(e);
        }
    }
}
