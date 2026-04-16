package com.example.AirBnb.App.dto;

import com.example.AirBnb.App.entities.enums.Gender;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private String gender;
    private LocalDate dateOfBirth;
}
