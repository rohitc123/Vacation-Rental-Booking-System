package com.example.AirBnb.App.controller;

import com.example.AirBnb.App.dto.InventoryDto;
import com.example.AirBnb.App.dto.RoomDto;
import com.example.AirBnb.App.dto.UpdateInventoryRequestDto;
import com.example.AirBnb.App.entities.Inventory;
import com.example.AirBnb.App.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<List<InventoryDto>> getAllInventoryByRoom(@PathVariable Long roomId){
        return ResponseEntity.ok(inventoryService.getAllInventoryByRoom(roomId));
    }

    @PatchMapping("/rooms/{roomId}")
    public ResponseEntity<Void> updateInventoryByRoomId(@PathVariable Long roomId, @RequestBody UpdateInventoryRequestDto updateInventoryRequestDto){
        inventoryService.updateInventoryByRoomId(roomId,updateInventoryRequestDto);
        return ResponseEntity.noContent().build();
    }
}
