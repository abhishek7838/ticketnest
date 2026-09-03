package com.ticketnest.payment.dto;

import java.math.BigDecimal;

public record CreatePaymentRequest(Long bookingId, BigDecimal amount) {}