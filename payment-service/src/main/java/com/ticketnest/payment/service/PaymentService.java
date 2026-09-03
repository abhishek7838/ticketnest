package com.ticketnest.payment.service;

import com.ticketnest.payment.dto.*;
import com.ticketnest.payment.entity.Payment;
import com.ticketnest.payment.entity.PaymentStatus;
import com.ticketnest.payment.repository.PaymentRepository;
import com.ticketnest.payment.security.WebhookVerifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final WebhookVerifier webhookVerifier;

    public PaymentService(PaymentRepository paymentRepository, WebhookVerifier webhookVerifier) {
        this.paymentRepository = paymentRepository;
        this.webhookVerifier = webhookVerifier;
    }

    /** Start a payment for a booking. Returns a PENDING payment with a gateway reference. */
    @Transactional
    public PaymentResponse create(CreatePaymentRequest req) {
        String gatewayRef = "pay_" + UUID.randomUUID();   // mirrors a real gateway order id
        Payment payment = paymentRepository.save(Payment.builder()
                .bookingId(req.bookingId())
                .amount(req.amount())
                .status(PaymentStatus.PENDING)
                .gatewayRef(gatewayRef)
                .createdAt(LocalDateTime.now())
                .build());
        return toResponse(payment);
    }

    /**
     * Handle a "payment result" webhook from the gateway.
     * We VERIFY the signature before trusting anything.
     */
    @Transactional
    public PaymentResponse handleWebhook(WebhookPayload payload) {
        // the message the signature is computed over: gatewayRef + ":" + status
        String message = payload.gatewayRef() + ":" + payload.status();

        // THE SECURITY GATE: reject anything whose signature doesn't match
        if (!webhookVerifier.isValid(message, payload.signature())) {
            throw new SecurityException("Invalid webhook signature — rejected");
        }

        Payment payment = paymentRepository.findByGatewayRef(payload.gatewayRef())
                .orElseThrow(() -> new RuntimeException("Payment not found: " + payload.gatewayRef()));

        if ("SUCCESS".equals(payload.status())) {
            payment.setStatus(PaymentStatus.SUCCESS);
            // TICK-16: here we'll call the Booking service to confirm the booking (Step 7)
        } else {
            payment.setStatus(PaymentStatus.FAILED);
        }
        return toResponse(paymentRepository.save(payment));
    }

    @Transactional(readOnly = true)
    public PaymentResponse getById(Long id) {
        Payment p = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + id));
        return toResponse(p);
    }

    private PaymentResponse toResponse(Payment p) {
        return new PaymentResponse(p.getId(), p.getBookingId(), p.getAmount(),
                p.getStatus(), p.getGatewayRef());
    }
}