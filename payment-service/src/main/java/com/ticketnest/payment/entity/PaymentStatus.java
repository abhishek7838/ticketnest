package com.ticketnest.payment.entity;

public enum PaymentStatus {
    PENDING,   // payment created, awaiting the gateway result
    SUCCESS,   // gateway confirmed payment (signature verified)
    FAILED     // payment failed or signature rejected
}