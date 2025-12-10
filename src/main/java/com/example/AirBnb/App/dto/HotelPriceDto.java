package com.example.AirBnb.App.dto;

import com.example.AirBnb.App.entities.Hotel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelPriceDto {
    private Hotel hotel;
    private double price;
}
