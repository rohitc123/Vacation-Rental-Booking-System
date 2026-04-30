package com.example.AirBnb.App.controller;

import com.example.AirBnb.App.dto.BookingDto;
import com.example.AirBnb.App.dto.BookingRequest;
import com.example.AirBnb.App.dto.GuestDto;
import com.example.AirBnb.App.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class HotelBookingController {

    private final BookingService bookingService;

    @PostMapping("/init")
    public ResponseEntity<BookingDto> initialiseBooking(@RequestBody BookingRequest bookingRequest){
        return ResponseEntity.ok(bookingService.initialiseBooking(bookingRequest));
    }

    @PostMapping("/{bookingId}/addGuest")
    public ResponseEntity<BookingDto> addGuests(@PathVariable Long bookingId, @RequestBody List<GuestDto> guestDtoList){
        return ResponseEntity.ok(bookingService.addGuests(bookingId,guestDtoList));
    }

    @PutMapping("{bookingId}/guest/{guestId}")
    public ResponseEntity<GuestDto> updateGuestById(@PathVariable Long guestId,@PathVariable Long bookingId,@RequestBody GuestDto guestDto){
        return ResponseEntity.ok(bookingService.updateGuestById(guestId,bookingId,guestDto));
    }

    @DeleteMapping("{bookingId}/guest/{guestId}/delete")
    public ResponseEntity<Void> deleteGuest(@PathVariable Long guestId,@PathVariable Long bookingId) {
        bookingService.deleteGuest(guestId,bookingId);
        return  ResponseEntity.noContent().build();
    }

    @PostMapping("/{bookingId}/payments")
    public ResponseEntity<Map<String,String>> initiatePayment(@PathVariable Long bookingId){
        String sessionUrl=bookingService.initiatePayment(bookingId);
        return ResponseEntity.ok(Map.of("SessionUrl",sessionUrl));
    }

    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long bookingId){
        bookingService.cancelBookingWithRefund(bookingId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{bookingId}/status")
    public ResponseEntity<Map<String,String>> getBookingStatus(@PathVariable Long bookingId){
        return ResponseEntity.ok(Map.of("Status:",bookingService.getBookingStatus(bookingId)));
    }
    
}
