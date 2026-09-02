package com.ticketnest.booking.service;

import com.ticketnest.booking.dto.*;
import com.ticketnest.booking.entity.Booking;
import com.ticketnest.booking.entity.BookingStatus;
import com.ticketnest.booking.repository.BookingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final SeatHoldService seatHoldService;

    public BookingService(BookingRepository bookingRepository, SeatHoldService seatHoldService) {
        this.bookingRepository = bookingRepository;
        this.seatHoldService = seatHoldService;
    }

    /** Hold seats atomically; if won, save a PENDING booking. */
    @Transactional
    public BookingResponse hold(HoldRequest req) {
        // a unique id for THIS user's hold attempt (this is what we store in Redis)
        String holderId = UUID.randomUUID().toString();

        boolean won = seatHoldService.tryHoldAll(req.eventId(), req.seats(), holderId);
        if (!won) {
            throw new RuntimeException("One or more seats are already taken");  // TICK-10: return 409
        }

        Booking booking = bookingRepository.save(Booking.builder()
                .userId(req.userId())
                .eventId(req.eventId())
                .seats(new HashSet<>(req.seats()))
                .holderId(holderId)
                .status(BookingStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build());

        return toResponse(booking);
    }

    /** Confirm a PENDING booking: re-check the hold, mark seats sold, flip to CONFIRMED. */
    @Transactional
    public BookingResponse confirm(Long bookingId, ConfirmRequest req) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found: " + bookingId));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new RuntimeException("Booking is not pending");
        }
        if (!booking.getUserId().equals(req.userId())) {
            throw new RuntimeException("This booking belongs to another user");
        }

        List<String> seats = List.copyOf(booking.getSeats());

        // THE MID-PAYMENT GUARD: do we still own the hold? (it may have expired after 5 min)
        if (!seatHoldService.ownsAll(booking.getEventId(), seats, booking.getHolderId())) {
            booking.setStatus(BookingStatus.EXPIRED);
            bookingRepository.save(booking);
            throw new RuntimeException("Your hold expired — please select seats again");
        }

        // promote: mark seats permanently SOLD and drop the temporary holds
        seatHoldService.markSoldAll(booking.getEventId(), seats);

        booking.setStatus(BookingStatus.CONFIRMED);   // TICK-15: real payment happens before this in Step 6
        return toResponse(bookingRepository.save(booking));
    }

    @Transactional(readOnly = true)
    public BookingResponse getById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found: " + id));
        return toResponse(booking);
    }

    private BookingResponse toResponse(Booking b) {
        return new BookingResponse(b.getId(), b.getUserId(), b.getEventId(),
                b.getSeats(), b.getStatus(), b.getCreatedAt());
    }
}
