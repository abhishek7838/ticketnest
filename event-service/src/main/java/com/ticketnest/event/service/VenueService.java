package com.ticketnest.event.service;

import com.ticketnest.event.dto.VenueRequest;
import com.ticketnest.event.dto.VenueResponse;
import com.ticketnest.event.entity.Venue;
import com.ticketnest.event.repository.VenueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class VenueService {

    private final VenueRepository repository;

    public VenueService(VenueRepository repository) {   // constructor injection
        this.repository = repository;
    }

    @Transactional
    public VenueResponse create(VenueRequest req) {
        Venue saved = repository.save(Venue.builder()
                .name(req.name()).address(req.address())
                .city(req.city()).capacity(req.capacity()).build());
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<VenueResponse> getAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    private VenueResponse toResponse(Venue v) {
        return new VenueResponse(v.getId(), v.getName(), v.getAddress(), v.getCity(), v.getCapacity());
    }
}