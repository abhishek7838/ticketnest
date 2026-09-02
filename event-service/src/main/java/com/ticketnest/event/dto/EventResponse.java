package com.ticketnest.event.dto;

import com.ticketnest.event.entity.EventCategory;
import com.ticketnest.event.entity.EventStatus;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventResponse(Long id, String title, String description, EventCategory category,
                            EventStatus status, LocalDateTime startsAt, BigDecimal basePrice,
                            VenueResponse venue) implements Serializable {}