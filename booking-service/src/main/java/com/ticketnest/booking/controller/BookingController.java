package com.ticketnest.booking.controller;

import com.ticketnest.booking.dto.*;
import com.ticketnest.booking.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/hold")
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse hold(@RequestBody HoldRequest request) {
        return bookingService.hold(request);
    }

    @PostMapping("/{id}/confirm")
    public BookingResponse confirm(@PathVariable Long id, @RequestBody ConfirmRequest request) {
        return bookingService.confirm(id, request);
    }

    @GetMapping("/{id}")
    public BookingResponse getById(@PathVariable Long id) {
        return bookingService.getById(id);
    }
}