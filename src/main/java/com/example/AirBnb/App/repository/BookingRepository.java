package com.example.AirBnb.App.repository;

import com.example.AirBnb.App.entities.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking,Long> {
}
