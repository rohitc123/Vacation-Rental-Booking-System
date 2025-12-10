package com.example.AirBnb.App.service;

import com.example.AirBnb.App.dto.HotelDto;
import com.example.AirBnb.App.dto.HotelInfoDto;
import org.springframework.stereotype.Service;


public interface HotelService {

    HotelDto createHotel(HotelDto hotelDto);

    HotelDto getHotelById(Long id);

    HotelDto updateHotelById(Long hotelId,HotelDto hotelDto);

    void deleteHotelById(Long hotelId);

    void activateHotel(Long hotelId);

    HotelInfoDto getHotelInfoById(Long hotelId);
}
