package com.picksy.roomservice.controller;


import com.picksy.roomservice.model.PollDTO;
import com.picksy.roomservice.request.RoomActionRequest;
import com.picksy.roomservice.request.RoomCreateRequest;
import com.picksy.roomservice.response.RoomDTO;
import com.picksy.roomservice.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/room")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @Operation(
            summary = "Create a new room",
            description = "Creates a new room for voting using the provided request body."
    )
    @PostMapping("/secure/create")
    public ResponseEntity<RoomDTO> createRoom( @RequestHeader ("X-User-Id") Long userId,
            @Parameter(description = "Room creation details") @RequestBody RoomCreateRequest request
    ) {
        return ResponseEntity.ok(roomService.createRoom(request, userId));
    }

    @Operation(
            summary = "Start voting in a room",
            description = "Starts the voting process in a room. Only the room owner can trigger this action."
    )
    @PostMapping("/secure/start")
    public ResponseEntity<String> startVoting(@RequestHeader ("X-User-Id") Long userId, @RequestBody RoomActionRequest request) throws BadRequestException {
        roomService.startVoting(userId, request.roomCode());
        return ResponseEntity.ok("Voting started.");
    }

    @Operation(
            summary = "Move to next category in room",
            description = "Changes the current category in the room to the next one. Only the room owner can perform this action."
    )
    @PostMapping("/secure/next")
    public ResponseEntity<String> nextCategory(@RequestHeader ("X-User-Id") Long userId, @RequestBody RoomActionRequest request) throws BadRequestException {
        roomService.nextCategory(userId, request.roomCode());
        return ResponseEntity.ok("Current category changed.");
    }

    @Operation(
            summary = "Finish voting in a room",
            description = "Ends the voting process for a room. Only the room owner can trigger this action."
    )
    @PostMapping("/secure/finish")
    public ResponseEntity<String> finishVoting(@RequestHeader ("X-User-Id") Long userId, @RequestBody RoomActionRequest request) throws BadRequestException {
        roomService.endVoting(request.roomCode(), userId);
        return ResponseEntity.ok("Room closed.");
    }

    @Operation(
            summary = "Close a room",
            description = "Closes the room and prevents further voting. Only the room owner can trigger this action."
    )
    @PostMapping("/secure/close")
    public ResponseEntity<String> closeRoom(@RequestHeader ("X-User-Id") Long userId, @RequestBody RoomActionRequest request) throws BadRequestException {
        roomService.closeRoom(userId, request.roomCode());
        return ResponseEntity.ok("Room closed.");
    }

    @Operation(
            summary = "Get room details",
            description = "Fetches details of the room including current category, participants, and voting status."
    )
    @GetMapping("/public/{roomCode}/details")
    public ResponseEntity<RoomDTO> getRoomDetails(
            @Parameter(description = "Code of the room to fetch details for") @PathVariable String roomCode
    ) throws BadRequestException {
        return ResponseEntity.ok(roomService.getRoomDetails(roomCode));
    }

    @Operation(
            summary = "Get voting results",
            description = "Fetches polls results including categories and choices"
    )
    @GetMapping("/public/{roomCode}/results")
    public ResponseEntity<List<PollDTO>> getPollResults(@PathVariable String roomCode) throws BadRequestException {
        return ResponseEntity.ok().body(roomService.getPolls(roomCode));
    }

    @Operation(
            summary = "Get participants count"
    )
    @GetMapping("/public/{roomCode}/participants")
    public ResponseEntity<Integer> getParticipants(@PathVariable String roomCode) throws BadRequestException {
        return ResponseEntity.ok().body(roomService.getParticipantsCount(roomCode));
    }

    @GetMapping("/secure/history")
    public ResponseEntity<List<RoomDTO>> getUserHistory(@RequestHeader("X-User-Id") Long userId) throws BadRequestException {
        return ResponseEntity.ok().body(roomService.getAllClosedRoomsForUser(userId));
    }
}
