package com.example.AirBnb.App.service;

import com.example.AirBnb.App.dto.RoomDto;
import com.example.AirBnb.App.entities.Hotel;
import com.example.AirBnb.App.entities.Room;
import com.example.AirBnb.App.entities.User;
import com.example.AirBnb.App.exception.ForbiddenException;
import com.example.AirBnb.App.exception.ResourceNotFoundException;
import com.example.AirBnb.App.repository.HotelRepository;
import com.example.AirBnb.App.repository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.AirBnb.App.util.AppUtils.getCurrentUser;

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
        User user= getCurrentUser();
        if(user.equals(hotel.getOwner())){
            throw new ForbiddenException("this user does not own this hotel"+hotelId);
        }
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
        User user= getCurrentUser();
        if(user.equals(hotel.getOwner())){
            throw new ForbiddenException("this user does not own this hotel"+hotelId);
        }
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
        User user= getCurrentUser();
        if(user.equals(room.getHotel().getOwner())){
            throw new ForbiddenException("this user does not own this hotel"+room.getHotel().getId());
        }
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

        User user= getCurrentUser();
        if(user.equals(room.getHotel().getOwner())){
            throw new ForbiddenException("this user does not own this hotel"+room.getHotel().getId());
        }
        inventoryService.deleteAllInventories(room);
        roomRepository.delete(room);
        log.info("Deleted room with Id: {}", roomId);
    }

    @Override
    public RoomDto updateRoomById(Long roomId, Long hotelId, RoomDto roomDto) {
        Hotel hotel= hotelRepository
                .findById(hotelId)
                .orElseThrow(()->new ResourceNotFoundException("Hotel not found with id "+hotelId));
        User user= getCurrentUser();
        if(!user.equals(hotel.getOwner())){
            throw new ForbiddenException("this user does not own this hotel"+hotelId);
        }


        Room room= roomRepository
                .findById(roomId)
                .orElseThrow(()->new ResourceNotFoundException("Room not found with id "+roomId));

        if(!room.getHotel().getId().equals(hotelId)){
            throw new ForbiddenException("Room does not belong to hotel");
        }

        modelMapper.map(roomDto,room);
//        room.setId(roomId);

        //TODO:if price and inventory is updated then update the inventory for this room

        room=roomRepository.save(room);


        return modelMapper.map(room,RoomDto.class);
    }
}
