package com.ticketnest.payment.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long bookingId;      // which booking this payment is for

    @Column(nullable = false)
    private BigDecimal amount;   // money = BigDecimal, never double

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    // an id the "gateway" uses to reference this payment (like a Razorpay order id)
    @Column(nullable = false, unique = true)
    private String gatewayRef;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}