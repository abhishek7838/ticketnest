package com.ticketnest.event.service;

import com.ticketnest.event.dto.*;
import com.ticketnest.event.entity.Event;
import com.ticketnest.event.entity.EventStatus;
import com.ticketnest.event.entity.Venue;
import com.ticketnest.event.repository.EventRepository;
import com.ticketnest.event.repository.VenueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final VenueRepository venueRepository;

    public EventService(EventRepository eventRepository, VenueRepository venueRepository) {
        this.eventRepository = eventRepository;
        this.venueRepository = venueRepository;
    }

    @Transactional
    public EventResponse create(EventRequest req) {
        Venue venue = venueRepository.findById(req.venueId())
                .orElseThrow(() -> new RuntimeException("Venue not found: " + req.venueId())); // TICK-7: make 404
        Event saved = eventRepository.save(Event.builder()
                .title(req.title()).description(req.description())
                .category(req.category()).status(EventStatus.DRAFT)   // new events start as DRAFT
                .startsAt(req.startsAt()).basePrice(req.basePrice())
                .venue(venue).build());
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<EventResponse> getAll() {
        return eventRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public EventResponse getById(Long id) {
        Event e = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found: " + id));
        return toResponse(e);
    }

    private EventResponse toResponse(Event e) {
        Venue v = e.getVenue();
        VenueResponse venue = new VenueResponse(v.getId(), v.getName(), v.getAddress(), v.getCity(), v.getCapacity());
        return new EventResponse(e.getId(), e.getTitle(), e.getDescription(),
                e.getCategory(), e.getStatus(), e.getStartsAt(), e.getBasePrice(), venue);
    }
}