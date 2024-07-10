package com.krafton.api_server.api.room.controller;

import com.krafton.api_server.api.room.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.krafton.api_server.api.room.dto.RoomRequest.RoomCreateRequest;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/room")
@RestController
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<?> createRoom(@RequestBody RoomCreateRequest roomCreateRequest) {
        if (roomCreateRequest.getKakaoId() == null) {
            return ResponseEntity.badRequest().body("User ID cannot be null");
        }
        System.out.println("roomCreateRequest.getKakaoId() = " + roomCreateRequest.getKakaoId());
        Long roomId = roomService.createRoom(roomCreateRequest);
        return ResponseEntity.ok(roomId);
    }

    @PostMapping("/{roomId}/join")
    public void joinRoom(@PathVariable("roomId") Long roomId, @RequestBody RoomCreateRequest request) {
        roomService.joinRoom(roomId, request);
    }

    @PostMapping("/{roomId}/exit")
    public void exitRoom(@PathVariable("roomId") Long roomId, @RequestBody RoomCreateRequest request) {
        roomService.exitRoom(roomId, request);
    }

}
