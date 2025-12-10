package com.example.AirBnb.App.dto;

import com.example.AirBnb.App.entities.User;
import com.example.AirBnb.App.entities.enums.Gender;
import jakarta.persistence.*;
import lombok.Data;

@Data
public class GuestDto {
    private Long id;
    private User user;
    private Gender gender;
    private String name;
    private Integer age;
}
