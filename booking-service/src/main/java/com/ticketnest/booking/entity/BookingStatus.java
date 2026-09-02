package com.ticketnest.booking.entity;

public enum BookingStatus {
    PENDING,     // seats held, awaiting payment/confirmation
    CONFIRMED,   // paid & seats marked sold
    CANCELLED,   // released before confirmation
    EXPIRED      // hold lapsed (5-min TTL passed)
}