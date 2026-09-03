package com.ticketnest.payment.controller;

import com.ticketnest.payment.dto.*;
import com.ticketnest.payment.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse create(@RequestBody CreatePaymentRequest request) {
        return paymentService.create(request);
    }

    // the gateway calls this when a payment completes
    @PostMapping("/webhook")
    public PaymentResponse webhook(@RequestBody WebhookPayload payload) {
        return paymentService.handleWebhook(payload);
    }

    @GetMapping("/{id}")
    public PaymentResponse getById(@PathVariable Long id) {
        return paymentService.getById(id);
    }
}