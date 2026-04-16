package com.example.AirBnb.App.service;

import com.example.AirBnb.App.dto.BookingDto;
import com.example.AirBnb.App.dto.ProfileUpdateRequestDto;
import com.example.AirBnb.App.dto.UserDto;
import com.example.AirBnb.App.entities.User;

import java.util.List;

public interface UserService  {
    User getUserById(Long id);

    void updateProfile(ProfileUpdateRequestDto profileUpdateRequestDto);

    UserDto getMyProfile();
}
