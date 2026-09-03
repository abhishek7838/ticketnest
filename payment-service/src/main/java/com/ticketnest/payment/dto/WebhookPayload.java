package com.ticketnest.payment.dto;

// what the payment gateway POSTs to our webhook when a payment completes
public record WebhookPayload(String gatewayRef, String status, String signature) {}