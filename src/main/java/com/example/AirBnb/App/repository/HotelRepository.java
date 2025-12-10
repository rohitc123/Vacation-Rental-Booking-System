package com.example.AirBnb.App.repository;

import com.example.AirBnb.App.entities.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelRepository extends JpaRepository<Hotel,Long> {
}
