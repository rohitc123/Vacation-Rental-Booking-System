package com.example.AirBnb.App.dto;

import com.example.AirBnb.App.entities.Guest;
import com.example.AirBnb.App.entities.Hotel;
import com.example.AirBnb.App.entities.Room;
import com.example.AirBnb.App.entities.User;
import com.example.AirBnb.App.entities.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class BookingDto {
    private Long id;
    private Integer roomsCount;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private BookingStatus bookingStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<GuestDto> guests;
}
