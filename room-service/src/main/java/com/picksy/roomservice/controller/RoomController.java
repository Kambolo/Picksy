package com.picksy.roomservice.controller;

import com.picksy.roomservice.message.RoomMessage;
import com.picksy.roomservice.request.RoomActionRequest;
import com.picksy.roomservice.request.RoomCreateRequest;
import com.picksy.roomservice.response.RoomDTO;
import com.picksy.roomservice.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/room")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping("/secure/create")
    public ResponseEntity<RoomDTO> createRoom(@RequestBody RoomCreateRequest request){
        return ResponseEntity.ok().body(roomService.createRoom(request));
    }

    @PostMapping("/secure/start")
    public ResponseEntity<String> startVoting(@RequestBody RoomActionRequest request) throws BadRequestException {
        roomService.startVoting(request.ownerId(), request.roomCode());
        return ResponseEntity.ok().body("Voting started.");
    }

    @PostMapping("/secure/next")
    public ResponseEntity<String> nextCategory(@RequestBody RoomActionRequest request) throws BadRequestException {
        roomService.nextCategory(request.ownerId(), request.roomCode());
        return ResponseEntity.ok().body("Current category changed.");
    }

    @PostMapping("/secure/close")
    public ResponseEntity<String> closeRoom(@RequestBody RoomActionRequest request) throws BadRequestException {
        roomService.closeRoom(request.ownerId(), request.roomCode());
        return ResponseEntity.ok().body("Room closed.");
    }

    @PostMapping("/public/{roomCode}/details")
    public ResponseEntity<RoomDTO> getRoomDetails(@PathVariable String roomCode) throws BadRequestException {
        return ResponseEntity.ok().body(roomService.getRoomDetails(roomCode));
    }

    @MessageMapping("/room/{roomCode}/join")
    public RoomMessage joinRoom(@DestinationVariable String roomCode, @Payload RoomMessage roomMessage) throws BadRequestException {
        roomService.joinRoom(roomCode, roomMessage);
        return roomMessage;
    }

    @MessageMapping("/room/{roomCode}/leave")
    public RoomMessage leaveRoom(@DestinationVariable String roomCode, @Payload RoomMessage roomMessage) throws BadRequestException {
        roomService.leaveRoom(roomCode, roomMessage);
        return roomMessage;
    }

}
