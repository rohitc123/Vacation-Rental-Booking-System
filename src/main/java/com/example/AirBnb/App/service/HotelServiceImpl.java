package com.example.AirBnb.App.service;

import com.example.AirBnb.App.dto.HotelDto;
import com.example.AirBnb.App.dto.HotelInfoDto;
import com.example.AirBnb.App.dto.RoomDto;
import com.example.AirBnb.App.entities.Hotel;
import com.example.AirBnb.App.entities.Room;
import com.example.AirBnb.App.entities.User;
import com.example.AirBnb.App.exception.ForbiddenException;
import com.example.AirBnb.App.exception.ResourceNotFoundException;
import com.example.AirBnb.App.repository.HotelRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService{

    private final HotelRepository hotelRepository;
    private final InventoryService inventoryService;
    private final RoomService roomService;
    private final ModelMapper modelMapper;

    @Override
    public HotelDto createHotel(HotelDto hotelDto) {
        log.info("Creating a new hotel with name: {}",hotelDto.getName());
        Hotel hotel= modelMapper.map(hotelDto,Hotel.class);
        hotel.setActive(false);
        User user= (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        hotel.setOwner(user);
        Hotel saveHotel=hotelRepository.save(hotel);
        log.info("Created a new hotel with Id: {}",hotel.getId());
        return modelMapper.map(saveHotel,HotelDto.class);
    }

    @Override
    public HotelDto getHotelById(Long id) {
        log.info("Getting a hotel with Id : {}",id);
        Hotel hotel= hotelRepository
                .findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Hotel not found with id "+id));
        User user= (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(user.equals(hotel.getOwner())){
            throw new ForbiddenException("this user does not own this hotel"+id);
        }
        return modelMapper.map(hotel,HotelDto.class);
    }

    @Override
    public HotelDto updateHotelById(Long hotelId, HotelDto hotelDto) {
        log.info("Updating a hotel with Id : {}",hotelId);
        Hotel hotel= hotelRepository
                .findById(hotelId)
                .orElseThrow(()->new ResourceNotFoundException("Hotel not found with id "+hotelId));

        User user= (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(user.equals(hotel.getOwner())){
            throw new ForbiddenException("this user does not own this hotel"+hotelId);
        }
        modelMapper.map(hotelDto,hotel);
        hotel.setId(hotelId);
        Hotel saveUpdatedHotel=hotelRepository.save(hotel);
        return modelMapper.map(saveUpdatedHotel,HotelDto.class);
    }

    @Override
    @Transactional
    public void deleteHotelById(Long hotelId) {
        log.info("deleting a hotel with Id : {}",hotelId);
        Hotel hotel= hotelRepository
                .findById(hotelId)
                .orElseThrow(()->new ResourceNotFoundException("Hotel not found with id "+hotelId));

        User user= (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(user.equals(hotel.getOwner())){
            throw new ForbiddenException("this user does not own this hotel"+hotelId);
        }

        for(Room room:hotel.getRooms()){
            inventoryService.deleteAllInventories(room);
            roomService.deleteRoomById(room.getId());
        }
        hotelRepository.deleteById(hotelId);
        log.info("deleted a hotel with Id : {}",hotelId);
    }

    @Override
    @Transactional
    public void activateHotel(Long hotelId) {
        log.info("Activating a hotel with Id : {}",hotelId);
        Hotel hotel= hotelRepository
                .findById(hotelId)
                .orElseThrow(()->new ResourceNotFoundException("Hotel not found with id "+hotelId));
        User user= (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(user.equals(hotel.getOwner())){
            throw new ForbiddenException("this user does not own this hotel"+hotelId);
        }
        hotel.setActive(true);
        hotelRepository.save(hotel);
        for(Room room: hotel.getRooms()){
            inventoryService.initializeRoomForAYear(room);
        }
        log.info("Activated a hotel with Id : {}",hotelId);
    }

    @Override
    public HotelInfoDto getHotelInfoById(Long hotelId) {
        Hotel hotel= hotelRepository
                .findById(hotelId)
                .orElseThrow(()->new ResourceNotFoundException("Hotel not found with id "+hotelId));
        List<RoomDto> rooms= hotel.getRooms()
                .stream().map((element) -> modelMapper.map(element, RoomDto.class))
                .toList();
        return new HotelInfoDto(modelMapper.map(hotel,HotelDto.class),rooms);

    }
}
