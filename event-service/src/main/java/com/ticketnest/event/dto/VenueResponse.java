package com.ticketnest.event.dto;

import java.io.Serializable;

public record VenueResponse(Long id, String name, String address, String city, Integer capacity)
        implements Serializable {}