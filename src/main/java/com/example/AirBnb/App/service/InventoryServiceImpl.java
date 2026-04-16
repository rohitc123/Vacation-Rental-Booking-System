package com.example.AirBnb.App.service;

import com.example.AirBnb.App.dto.HotelPriceDto;
import com.example.AirBnb.App.dto.HotelSearchRequest;
import com.example.AirBnb.App.dto.InventoryDto;
import com.example.AirBnb.App.dto.UpdateInventoryRequestDto;
import com.example.AirBnb.App.entities.Inventory;
import com.example.AirBnb.App.entities.Room;
import com.example.AirBnb.App.entities.User;
import com.example.AirBnb.App.exception.ResourceNotFoundException;
import com.example.AirBnb.App.repository.HotelMinPriceRepository;
import com.example.AirBnb.App.repository.InventoryRepository;
import com.example.AirBnb.App.repository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.AirBnb.App.util.AppUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService{

    private  final InventoryRepository inventoryRepository;
    private final ModelMapper modelMapper;
    private final HotelMinPriceRepository hotelMinPriceRepository;
    private final RoomRepository roomRepository;

    @Override
    public void initializeRoomForAYear(Room room) {
        LocalDate today=LocalDate.now();
        LocalDate endDate=today.plusYears(1);
        for(; !today.isAfter(endDate);today=today.plusDays(1)){

            Inventory inventory=Inventory.builder()
                    .hotel(room.getHotel())
                    .room(room)
                    .bookedCount(0)
                    .reservedCount(0)
                    .city(room.getHotel().getCity())
                    .date(today)
                    .price(room.getBasePrice())
                    .surgeFactor(BigDecimal.ONE)
                    .totalCount(room.getTotalCount())
                    .closed(false)
                    .build();
            inventoryRepository.save(inventory);
        }



    }

    @Override
    public void deleteAllInventories(Room room) {
        log.info("deleting inventory of room with Id {}",room.getId());
        LocalDate today= LocalDate.now();
        inventoryRepository.deleteByRoom(room);
    }

    @Override
    public Page<HotelPriceDto> searchHotels(HotelSearchRequest hotelSearchRequest) {
        log.info("Searching hotel for {} city,from {} to {}",hotelSearchRequest.getCity(),hotelSearchRequest.getStartDate(),hotelSearchRequest.getEndDate());
        Pageable pageable= PageRequest.of(hotelSearchRequest.getPage(),hotelSearchRequest.getSize());
        long dateCount=
                ChronoUnit.DAYS.between(hotelSearchRequest.getStartDate(),hotelSearchRequest.getEndDate())+1;

        //business logic-90 days
        Page<HotelPriceDto> hotelPage=hotelMinPriceRepository.findHotelsWithAvailableInventory(hotelSearchRequest.getCity(), hotelSearchRequest.getStartDate(),hotelSearchRequest.getEndDate(),hotelSearchRequest.getRoomsCount(),dateCount,pageable);
        return hotelPage;
    }

    @Override
    public List<InventoryDto> getAllInventoryByRoom(Long roomId) {
        log.info("Getting all inventory by room in given date range");
        Room room= roomRepository.findById(roomId)
                .orElseThrow(()->new ResourceNotFoundException("Room not found with id"+roomId));
        User user=getCurrentUser();

        if((!user.getId().equals(room.getHotel().getOwner().getId()))){
            throw new AccessDeniedException("you are not owner of this hotel");
        }

        return inventoryRepository.findByRoomOrderByDate(room)
                .stream()
                .map(inv -> modelMapper.map(inv, InventoryDto.class))
                .toList();


    }

    @Override
    @Transactional
    public void updateInventoryByRoomId(Long roomId, UpdateInventoryRequestDto updateInventoryRequestDto) {
        log.info("updating inventory by room id {} in given date range between {}-{}",roomId,
                updateInventoryRequestDto.getStartDate(),updateInventoryRequestDto.getEndDate());
        Room room= roomRepository.findById(roomId)
                .orElseThrow(()->new ResourceNotFoundException("Room not found with id"+roomId));
        User user=getCurrentUser();

        if((!user.getId().equals(room.getHotel().getOwner().getId()))){
            throw new AccessDeniedException("you are not owner of this hotel");
        }

        inventoryRepository.updateInventory(roomId,updateInventoryRequestDto.getStartDate(),
                updateInventoryRequestDto.getEndDate(),updateInventoryRequestDto.getSurgeFactor(),updateInventoryRequestDto.getClosed());

        log.info("Inventory updated successfully");
    }
}
