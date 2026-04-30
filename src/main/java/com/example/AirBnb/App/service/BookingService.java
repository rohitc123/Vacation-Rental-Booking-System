package com.example.AirBnb.App.service;

import com.example.AirBnb.App.dto.BookingDto;
import com.example.AirBnb.App.dto.BookingRequest;
import com.example.AirBnb.App.dto.GuestDto;
import com.example.AirBnb.App.dto.HotelReportDto;
import com.example.AirBnb.App.entities.Booking;
import com.stripe.model.Event;

import java.time.LocalDate;
import java.util.List;

public interface BookingService {
    BookingDto initialiseBooking(BookingRequest bookingRequest);

    BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList);

    void deleteGuest(Long guestId, Long bookingId);

    String initiatePayment(Long bookingId);

    void capturePayment(Event event);

    Booking cancelBooking(Long bookingId);

    String getBookingStatus(Long bookingId);

    List<BookingDto> getAllBookingsByHotelId(Long hotelId);

    HotelReportDto getHotelReport(Long hotelId, LocalDate startDate, LocalDate endDate);

    List<BookingDto> getMyBooking();

    GuestDto updateGuestById(Long guestId,Long bookingId, GuestDto guestDto);

    void cancelBookingWithRefund(Long bookingId);
}
