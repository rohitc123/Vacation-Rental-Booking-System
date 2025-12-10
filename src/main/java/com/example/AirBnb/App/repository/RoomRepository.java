package com.example.AirBnb.App.repository;

import com.example.AirBnb.App.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room,Long> {
}
