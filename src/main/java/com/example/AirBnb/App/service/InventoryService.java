package com.example.AirBnb.App.service;

import com.example.AirBnb.App.dto.HotelDto;
import com.example.AirBnb.App.dto.HotelPriceDto;
import com.example.AirBnb.App.dto.HotelSearchRequest;
import com.example.AirBnb.App.entities.Room;
import org.springframework.data.domain.Page;

public interface InventoryService {

    void initializeRoomForAYear(Room room);

    void deleteAllInventories(Room room);

    Page<HotelPriceDto> searchHotels(HotelSearchRequest hotelSearchRequest);
}
