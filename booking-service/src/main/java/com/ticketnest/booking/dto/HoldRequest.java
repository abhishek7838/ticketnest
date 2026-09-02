package com.ticketnest.booking.dto;

import java.util.List;

public record HoldRequest(Long eventId, Long userId, List<String> seats) {}