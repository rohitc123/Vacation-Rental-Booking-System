package com.example.AirBnb.App.service;

import com.example.AirBnb.App.dto.BookingDto;
import com.example.AirBnb.App.dto.BookingRequest;
import com.example.AirBnb.App.dto.GuestDto;
import com.example.AirBnb.App.entities.*;
import com.example.AirBnb.App.entities.enums.BookingStatus;
import com.example.AirBnb.App.exception.ResourceNotFoundException;
import com.example.AirBnb.App.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService{
    private final GuestRepository guestRepository;

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final InventoryRepository inventoryRepository;
    private final BookingRepository bookingRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public BookingDto initialiseBooking(BookingRequest bookingRequest) {

        log.info("initializing booking for hotel:{},room:{},date {}---{}",
                bookingRequest.getHotelId(),bookingRequest.getRoomId(),bookingRequest.getCheckInDate(),bookingRequest.getCheckoutDate());
        //Validate hotel exists
        Hotel hotel= hotelRepository
                .findById(bookingRequest.getHotelId())
                .orElseThrow(()->new ResourceNotFoundException("Hotel not found with id "+bookingRequest.getHotelId()));

        //Validate room exists
        Room room= roomRepository
                .findById(bookingRequest.getRoomId())
                .orElseThrow(()->new ResourceNotFoundException("Room not found with id "+bookingRequest.getHotelId()));

        //Ensure availability for date range
        List<Inventory> inventoryList=inventoryRepository.findAndLockAvailableInventory(room.getId(),bookingRequest.getCheckInDate(),
                bookingRequest.getCheckoutDate(),bookingRequest.getRoomCount());

        long dayCount= ChronoUnit.DAYS.between(bookingRequest.getCheckInDate(),bookingRequest.getCheckoutDate())+1;
        if(inventoryList.size()!=dayCount){
            throw new IllegalStateException("Room is not available anymore");
        }

        //reserve the room/update the booked count

        for(Inventory inventory:inventoryList){
            inventory.setReservedCount(inventory.getReservedCount()+bookingRequest.getRoomCount());
        }

        inventoryRepository.saveAll(inventoryList);

        //create the booking



        Booking booking=Booking.builder()
                .bookingStatus(BookingStatus.RESERVED)
                .hotel(hotel)
                .room(room)
                .checkInDate(bookingRequest.getCheckInDate())
                .checkOutDate(bookingRequest.getCheckoutDate())
                .user(getCurrentUser())
                .roomsCount(bookingRequest.getRoomCount())
                .amount(BigDecimal.TEN)
                .build();

        booking=bookingRepository.save(booking);


        return modelMapper.map(booking,BookingDto.class);
    }

    @Override
    public BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList) {

        log.info("Adding guest with booking id:{}",bookingId);

        Booking booking= bookingRepository.findById(bookingId)
                .orElseThrow(()->new ResourceNotFoundException("Booking id not found"+bookingId));

        //check  booking is expired or not
        if(hasBookingExpired(booking)){
            throw new IllegalStateException("booking has already expired");
        }

        //check booking status is reserved or not
        if(booking.getBookingStatus()!=BookingStatus.RESERVED){
            throw  new IllegalStateException("booking is not under reserved state , cannot add guest");
        }

        //Adding guest while booking
        for(GuestDto guestDto:guestDtoList){
            Guest guest=modelMapper.map(guestDto,Guest.class);
            guest.setUser(getCurrentUser());
            guest=guestRepository.save(guest);
            booking.getGuests().add(guest);
        }

        booking.setBookingStatus(BookingStatus.GUESTS_ADDED);
        booking=bookingRepository.save(booking);
        return modelMapper.map(booking,BookingDto.class);
    }


    public boolean hasBookingExpired(Booking booking){
        return booking.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now());
    }

    public User getCurrentUser(){
        User user=new User();
        user.setId(1L);
        return user;
    }
}