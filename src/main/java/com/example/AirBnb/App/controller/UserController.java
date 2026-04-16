package com.example.AirBnb.App.controller;

import com.example.AirBnb.App.dto.BookingDto;
import com.example.AirBnb.App.dto.ProfileUpdateRequestDto;
import com.example.AirBnb.App.dto.UserDto;
import com.example.AirBnb.App.service.BookingService;
import com.example.AirBnb.App.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final BookingService bookingService;

    @PatchMapping("/profile")
    public ResponseEntity<Void> updateProfile(@RequestBody ProfileUpdateRequestDto profileUpdateRequestDto){
        userService.updateProfile(profileUpdateRequestDto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDto> getMyProfile(){
        return ResponseEntity.ok(userService.getMyProfile());
    }

    @GetMapping("/booking")
    public ResponseEntity<List<BookingDto>> getMyBooking(){
        return ResponseEntity.ok(bookingService.getMyBooking());
    }

}
