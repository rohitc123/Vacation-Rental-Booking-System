package com.example.AirBnb.App.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ProfileUpdateRequestDto {
    private String name;
    private LocalDate dateOfBirth;
    private String gender;
    private String city;
}
