package com.ticketnest.event.controller;

import com.ticketnest.event.dto.EventRequest;
import com.ticketnest.event.dto.EventResponse;
import com.ticketnest.event.service.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService service;

    public EventController(EventService service) { this.service = service; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponse create(@RequestBody EventRequest request) { return service.create(request); }

    @GetMapping
    public List<EventResponse> getAll() { return service.getAll(); }

    @GetMapping("/{id}")
    public EventResponse getById(@PathVariable Long id) { return service.getById(id); }
}