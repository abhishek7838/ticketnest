package com.ticketnest.event.dto;

import com.ticketnest.event.entity.EventCategory;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventRequest(String title, String description, EventCategory category,
                           LocalDateTime startsAt, BigDecimal basePrice, Long venueId) {}