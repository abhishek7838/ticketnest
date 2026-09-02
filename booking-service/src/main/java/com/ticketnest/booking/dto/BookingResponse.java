package com.ticketnest.booking.dto;

import com.ticketnest.booking.entity.BookingStatus;
import java.time.LocalDateTime;
import java.util.Set;

public record BookingResponse(Long id, Long userId, Long eventId, Set<String> seats,
                              BookingStatus status, LocalDateTime createdAt) {}