package com.example.AirBnb.App.repository;

import com.example.AirBnb.App.entities.Booking;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking,Long> {
    Optional<Booking> findByPaymentSessionId(String sessionId);
}
