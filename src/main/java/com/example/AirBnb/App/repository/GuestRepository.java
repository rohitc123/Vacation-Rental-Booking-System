package com.example.AirBnb.App.repository;

import com.example.AirBnb.App.entities.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestRepository extends JpaRepository<Guest, Long> {
}