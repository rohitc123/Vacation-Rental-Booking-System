package com.example.AirBnb.App.service;

import com.example.AirBnb.App.dto.BookingDto;
import com.example.AirBnb.App.dto.BookingRequest;
import com.example.AirBnb.App.dto.GuestDto;
import com.stripe.model.Event;

import java.util.List;

public interface BookingService {
    BookingDto initialiseBooking(BookingRequest bookingRequest);

    BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList);

    String initiatePayment(Long bookingId);

    void capturePayment(Event event);

    void cancelBooking(Long bookingId);

    String getBookingStatus(Long bookingId);
}
