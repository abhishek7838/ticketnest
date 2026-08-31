package com.ticketnest.event.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)                 // store the enum's NAME ("CONCERT"), not its number
    @Column(nullable = false)
    private EventCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status;

    @Column(nullable = false)
    private LocalDateTime startsAt;

    @Column(nullable = false)
    private BigDecimal basePrice;                // money = BigDecimal, never double

    @ManyToOne(fetch = FetchType.LAZY)           // many events -> one venue
    @JoinColumn(name = "venue_id", nullable = false)   // creates a venue_id foreign-key column
    private Venue venue;

    private Long organizerId;                    // TICK-6: will come from the JWT in Step 3
}