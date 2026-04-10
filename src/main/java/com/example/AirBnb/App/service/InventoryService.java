package com.example.AirBnb.App.service;

import com.example.AirBnb.App.dto.HotelPriceDto;
import com.example.AirBnb.App.dto.HotelSearchRequest;
import com.example.AirBnb.App.dto.InventoryDto;
import com.example.AirBnb.App.dto.UpdateInventoryRequestDto;
import com.example.AirBnb.App.entities.Inventory;
import com.example.AirBnb.App.entities.Room;
import org.springframework.data.domain.Page;

import java.util.List;

public interface InventoryService {

    void initializeRoomForAYear(Room room);

    void deleteAllInventories(Room room);

    Page<HotelPriceDto> searchHotels(HotelSearchRequest hotelSearchRequest);

    List<InventoryDto> getAllInventoryByRoom(Long roomId);

    void updateInventoryByRoomId(Long roomId, UpdateInventoryRequestDto updateInventoryRequestDto);
}
