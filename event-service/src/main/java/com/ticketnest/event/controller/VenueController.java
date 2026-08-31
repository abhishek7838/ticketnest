package com.ticketnest.event.controller;

import com.ticketnest.event.dto.VenueRequest;
import com.ticketnest.event.dto.VenueResponse;
import com.ticketnest.event.service.VenueService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/venues")
public class VenueController {

    private final VenueService service;

    public VenueController(VenueService service) { this.service = service; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VenueResponse create(@RequestBody VenueRequest request) { return service.create(request); }

    @GetMapping
    public List<VenueResponse> getAll() { return service.getAll(); }
}