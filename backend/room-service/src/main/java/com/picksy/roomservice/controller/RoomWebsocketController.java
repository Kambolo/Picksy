package com.picksy.roomservice.controller;

import com.picksy.roomservice.message.RoomMessage;
import com.picksy.roomservice.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class RoomWebsocketController {
    private final RoomService roomService;

    @MessageMapping("/public/room/{roomCode}/join")
    public RoomMessage joinRoom(@DestinationVariable String roomCode, @Payload RoomMessage roomMessage) throws BadRequestException {
        roomService.joinRoom(roomCode, roomMessage);
        return roomMessage;
    }

    @MessageMapping("/public/room/{roomCode}/leave")
    public RoomMessage leaveRoom(@DestinationVariable String roomCode, @Payload RoomMessage roomMessage) throws BadRequestException {
        roomService.leaveRoom(roomCode, roomMessage);
        return roomMessage;
    }
}
