package com.example.AirBnb.App.dto;

import lombok.Data;

@Data
public class SignUpRequestDto {
    private String name;
    private String password;
    private String email;
}
