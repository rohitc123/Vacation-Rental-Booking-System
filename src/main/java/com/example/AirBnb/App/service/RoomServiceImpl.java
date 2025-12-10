package com.example.AirBnb.App.service;

import com.example.AirBnb.App.dto.RoomDto;
import com.example.AirBnb.App.entities.Hotel;
import com.example.AirBnb.App.entities.Room;
import com.example.AirBnb.App.exception.ResourceNotFoundException;
import com.example.AirBnb.App.repository.HotelRepository;
import com.example.AirBnb.App.repository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService{
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final InventoryService inventoryService;
    private final ModelMapper modelMapper;


    @Transactional
    @Override
    public RoomDto createNewRoomInHotel(Long hotelId,RoomDto roomDto) {
        log.info("creating a new room in  hotel with Id : {}",hotelId);
        Hotel hotel= hotelRepository
                .findById(hotelId)
                .orElseThrow(()->new ResourceNotFoundException("Hotel not found with id "+hotelId));
        Room room =modelMapper.map(roomDto,Room.class);
        room.setHotel(hotel);
        Room saveRoom=roomRepository.save(room);

        //TODO:create Inventory as soon as room is created and if hotel is active
        if(hotel.isActive()){
            inventoryService.initializeRoomForAYear(room);
        }
        return modelMapper.map(saveRoom,RoomDto.class);
    }

    @Override
    public List<RoomDto> getAllRoomInHotel(Long hotelId) {
        log.info("Getting all room in  hotel with Id : {}",hotelId);
        Hotel hotel= hotelRepository
                .findById(hotelId)
                .orElseThrow(()->new ResourceNotFoundException("Hotel not found with id "+hotelId));
        return hotel.getRooms()
                .stream()
                .map((element) -> modelMapper.map(element, RoomDto.class))
                .collect(Collectors.toList());

    }

    @Override
    public RoomDto getRoomById(Long roomId) {
        log.info("Getting the  room  with Id : {}",roomId);
        Room room= roomRepository
                .findById(roomId)
                .orElseThrow(()->new ResourceNotFoundException("Room not found with id "+roomId));
        return modelMapper.map(room,RoomDto.class);
    }

    @Override
    public void deleteRoomById(Long roomId) {
        log.info("deleting the  room  with Id : {}",roomId);
        boolean isExist= roomRepository.existsById(roomId);
        if(!isExist){
            throw new ResourceNotFoundException("Room not found with id "+roomId);
        }
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id " + roomId));

        inventoryService.deleteAllInventories(room);
        roomRepository.delete(room);
        log.info("Deleted room with Id: {}", roomId);
    }
}
