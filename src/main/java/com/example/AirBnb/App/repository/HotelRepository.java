package com.example.AirBnb.App.repository;

import com.example.AirBnb.App.entities.Hotel;
import com.example.AirBnb.App.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HotelRepository extends JpaRepository<Hotel,Long> {
    List<Hotel> findByOwner(User user);
}
