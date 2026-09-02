package com.ticketnest.booking.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class SeatHoldService {

    private final StringRedisTemplate redis;

    // 5-minute hold window
    private static final Duration HOLD_TTL = Duration.ofMinutes(5);

    public SeatHoldService(StringRedisTemplate redis) {
        this.redis = redis;
    }

    // Key for a temporary HOLD on a seat (has a TTL)
    private String holdKey(Long eventId, String seat) {
        return "hold:event:" + eventId + ":seat:" + seat;
    }

    // Key for a permanent SOLD marker (no TTL) — set once a booking is confirmed
    private String soldKey(Long eventId, String seat) {
        return "sold:event:" + eventId + ":seat:" + seat;
    }

    /**
     * Try to hold ALL requested seats for this holder.
     * Returns true only if every seat was successfully held; otherwise it
     * releases any it managed to grab and returns false (all-or-nothing).
     */
    public boolean tryHoldAll(Long eventId, List<String> seats, String holderId) {
        List<String> grabbed = new ArrayList<>();
        for (String seat : seats) {
            // Already sold? Then it can never be held again.
            if (Boolean.TRUE.equals(redis.hasKey(soldKey(eventId, seat)))) {
                releaseAll(eventId, grabbed, holderId);   // roll back what we grabbed
                return false;
            }
            // THE ATOMIC HOLD: set the key only if it doesn't exist, with a 5-min TTL.
            Boolean won = redis.opsForValue()
                    .setIfAbsent(holdKey(eventId, seat), holderId, HOLD_TTL);
            if (Boolean.TRUE.equals(won)) {
                grabbed.add(seat);                        // we won this seat
            } else {
                releaseAll(eventId, grabbed, holderId);   // someone else holds it → roll back
                return false;
            }
        }
        return true;   // every seat held by us
    }

    /** Does this holder still own the hold on every one of these seats? (confirm-time guard) */
    public boolean ownsAll(Long eventId, List<String> seats, String holderId) {
        for (String seat : seats) {
            String current = redis.opsForValue().get(holdKey(eventId, seat));
            if (!holderId.equals(current)) {
                return false;   // hold expired or belongs to someone else
            }
        }
        return true;
    }

    /** Promote holds to permanent SOLD, and drop the temporary hold keys. */
    public void markSoldAll(Long eventId, List<String> seats) {
        for (String seat : seats) {
            redis.opsForValue().set(soldKey(eventId, seat), "SOLD");  // permanent, no TTL
            redis.delete(holdKey(eventId, seat));
        }
    }

    /** Release holds this holder owns (used on rollback / cancel). */
    public void releaseAll(Long eventId, List<String> seats, String holderId) {
        for (String seat : seats) {
            String current = redis.opsForValue().get(holdKey(eventId, seat));
            if (holderId.equals(current)) {
                redis.delete(holdKey(eventId, seat));
            }
        }
    }
}