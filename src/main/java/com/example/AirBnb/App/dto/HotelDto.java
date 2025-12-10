package com.example.AirBnb.App.dto;

import com.example.AirBnb.App.entities.HotelContactInfo;
import lombok.Data;


@Data
public class HotelDto {
    private String name;
    private String city;
    private String[] photos;
    private String[] amenities;
    private HotelContactInfo contactInfo;
    private Boolean active;
}
