package com.example.AirBnb.App.controller;

import com.example.AirBnb.App.dto.HotelDto;
import com.example.AirBnb.App.dto.HotelInfoDto;
import com.example.AirBnb.App.dto.HotelPriceDto;
import com.example.AirBnb.App.dto.HotelSearchRequest;
import com.example.AirBnb.App.service.HotelService;
import com.example.AirBnb.App.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
public class HotelBrowseController {

    private final InventoryService inventoryService;
    private final HotelService hotelService;

    @GetMapping("/search")
    public ResponseEntity<Page<HotelPriceDto>> searchHotels(@RequestBody HotelSearchRequest hotelSearchRequest){
        Page<HotelPriceDto> page=inventoryService.searchHotels(hotelSearchRequest);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{hotelId}/info")
    public ResponseEntity<HotelInfoDto> getHotelInfo(@PathVariable Long hotelId){
        return ResponseEntity.ok(hotelService.getHotelInfoById(hotelId));
    }

}
