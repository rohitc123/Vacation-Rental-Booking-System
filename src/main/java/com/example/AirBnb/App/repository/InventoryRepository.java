package com.example.AirBnb.App.repository;

import com.example.AirBnb.App.entities.Hotel;
import com.example.AirBnb.App.entities.Inventory;
import com.example.AirBnb.App.entities.Room;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory,Long> {
    void deleteByRoom(Room room);

    //// Finds hotels that have enough available rooms (considering booked and reserved) in a city
    ///  for a given date range, returning only hotels where inventory exists for all dates in the range

    @Query("""
            SELECT  DISTINCT i.hotel
            from Inventory i
            WHERE i.city= :city
            AND i.date BETWEEN :startDate AND :endDate
            AND i.closed = false
            And (i.totalCount - i.bookedCount-i.reservedCount) >= :roomsCount
            GROUP BY i.hotel,i.room
            HAVING COUNT(i.date)= :dateCount
            """)
    Page<Hotel> findHotelsWithAvailableInventory(
            @Param("city") String city,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate, @Param("roomsCount") Integer roomsCount,
            @Param("dateCount") Long dateCount,
            Pageable pageable
            );



    //// Retrieves and locks available inventory for a specific room and date range to safely
    /// update booked/reserved counts without race conditions

    @Query("""
            SELECT i FROM Inventory i WHERE i.room.id= :roomId
            AND i.date BETWEEN :startDate AND :endDate
            AND i.closed = false
            And (i.totalCount - i.bookedCount-i.reservedCount )>= :roomsCount
            """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Inventory> findAndLockAvailableInventory(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomsCount") Integer roomsCount
    );


    @Query("""
        SELECT i FROM Inventory i
        WHERE i.room.id = :roomId
          AND i.date BETWEEN :startDate AND :endDate
          AND i.closed = false
          And (i.totalCount - i.bookedCount) >= :roomsCount
        """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)  // lock rows to safely update
    List<Inventory> findAndLockReservedInventory(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomsCount") Integer roomsCount
    );

    @Modifying
    @Query(
            """
                    UPDATE Inventory i
                    SET i.reservedCount=i.reservedCount+:numberOfRooms
                    WHERE i.hotel.id = :hotelId
                    AND i.room.id = :roomId
                    AND i.date BETWEEN :startDate AND :endDate
                    And (i.totalCount - i.bookedCount-i.reservedCount) >= :numberOfRooms
                    AND i.closed = false
                    """
    )
    void  initBooking(
            @Param("hotelId") Long hotelId,
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("numberOfRooms") int numberOfRooms
    );



    //// Atomically confirms a booking by decrementing reservedCount and incrementing bookedCount for a room
    /// over a date range, ensuring availability and that the inventory is open

    @Modifying
    @Query(
            """
                    UPDATE Inventory i
                    SET i.reservedCount=i.reservedCount-:numberOfRooms,
                    i.bookedCount= i.bookedCount + :numberOfRooms
                    WHERE i.hotel.id = :hotelId
                    AND i.room.id = :roomId
                    AND i.date BETWEEN :startDate AND :endDate
                    AND (i.totalCount - i.bookedCount) >= :numberOfRooms
                    AND i.reservedCount >= :numberOfRooms
                    AND i.closed = false
                    """
    )
    void  conformBooking(
            @Param("hotelId") Long hotelId,
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("numberOfRooms") int numberOfRooms
    );

    @Modifying
    @Query("""
            UPDATE Inventory
            SET bookedCount = bookedCount - :numberOfRooms
            WHERE hotel.id = :hotelId
            AND room.id = :roomId
            AND date BETWEEN :startDate AND :endDate
            AND (totalCount - bookedCount) >= :numberOfRooms
            AND closed = false
            """)
    void  cancelBooking(
            @Param("hotelId") Long hotelId,
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("numberOfRooms") int numberOfRooms
    );

    List<Inventory> findByHotelAndDateBetween(Hotel hotel, LocalDate startDate, LocalDate endDate);
}
