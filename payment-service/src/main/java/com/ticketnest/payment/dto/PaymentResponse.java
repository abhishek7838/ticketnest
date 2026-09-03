package com.ticketnest.payment.dto;

import com.ticketnest.payment.entity.PaymentStatus;
import java.math.BigDecimal;

public record PaymentResponse(Long id, Long bookingId, BigDecimal amount,
                              PaymentStatus status, String gatewayRef) {}