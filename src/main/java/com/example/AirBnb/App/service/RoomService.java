package com.example.AirBnb.App.service;

import com.example.AirBnb.App.dto.RoomDto;

import java.util.List;

public interface RoomService {
    RoomDto createNewRoomInHotel(Long hotelId,RoomDto roomDto);
    List<RoomDto> getAllRoomInHotel(Long hotelId);
    RoomDto getRoomById(Long roomId);
    void deleteRoomById(Long roomId);
}
