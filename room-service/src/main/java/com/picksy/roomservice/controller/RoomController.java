package com.picksy.roomservice.controller;

import com.picksy.roomservice.model.PollDTO;
import com.picksy.roomservice.request.RoomActionRequest;
import com.picksy.roomservice.request.RoomCreateRequest;
import com.picksy.roomservice.response.PollDTOResponse;
import com.picksy.roomservice.response.RoomDTO;
import com.picksy.roomservice.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
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
            description = "Creates a new room for voting using the provided request body. The authenticated user becomes the room owner."
    )
    @PostMapping("/secure/create")
    public ResponseEntity<RoomDTO> createRoom(
            @RequestHeader("X-User-Id") Long userId,
            @Parameter(description = "Room creation details") @RequestBody RoomCreateRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(roomService.createRoom(request, userId));
    }

    @Operation(
            summary = "Start voting in a room",
            description = "Starts the voting process in a room. Only the room owner is allowed to perform this action."
    )
    @PatchMapping("/secure/start")
    public ResponseEntity<String> startVoting(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody RoomActionRequest request
    ) throws BadRequestException {
        roomService.startVoting(userId, request.roomCode());
        return ResponseEntity.ok("Voting started.");
    }

    @Operation(
            summary = "Move to the next category",
            description = "Moves the voting process to the next category in the room. Only the room owner is allowed to perform this action."
    )
    @PatchMapping("/secure/next")
    public ResponseEntity<String> nextCategory(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody RoomActionRequest request
    ) throws BadRequestException {
        roomService.nextCategory(userId, request.roomCode());
        return ResponseEntity.ok("Current category changed.");
    }

    @Operation(
            summary = "Finish voting",
            description = "Finishes the voting process in the room without closing it. Only the room owner is allowed to perform this action."
    )
    @PatchMapping("/secure/finish")
    public ResponseEntity<String> finishVoting(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody RoomActionRequest request
    ) throws BadRequestException {
        roomService.endVoting(request.roomCode(), userId);
        return ResponseEntity.ok("Voting finished.");
    }

    @Operation(
            summary = "Close a room",
            description = "Closes the room and prevents any further actions. Only the room owner is allowed to perform this action."
    )
    @PatchMapping("/secure/close")
    public ResponseEntity<String> closeRoom(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody RoomActionRequest request
    ) throws BadRequestException {
        roomService.closeRoom(userId, request.roomCode());
        return ResponseEntity.ok("Room closed.");
    }

    @Operation(
            summary = "Get room details",
            description = "Returns detailed information about the room, including current category, participants and voting status."
    )
    @GetMapping("/public/{roomCode}/details")
    public ResponseEntity<RoomDTO> getRoomDetails(
            @Parameter(description = "Unique room code") @PathVariable String roomCode
    ) throws BadRequestException {
        return ResponseEntity.ok(roomService.getRoomDetails(roomCode));
    }

    @Operation(
            summary = "Get voting results",
            description = "Returns voting results for all categories in the room."
    )
    @GetMapping("/public/{roomCode}/results")
    public ResponseEntity<List<PollDTOResponse>> getPollResults(
            @Parameter(description = "Unique room code") @PathVariable String roomCode
    ) throws BadRequestException {
        return ResponseEntity.ok(roomService.getPolls(roomCode));
    }

    @Operation(
            summary = "Get participants count",
            description = "Returns the number of participants currently present in the room."
    )
    @GetMapping("/public/{roomCode}/participants")
    public ResponseEntity<Integer> getParticipants(
            @Parameter(description = "Unique room code") @PathVariable String roomCode
    ) throws BadRequestException {
        return ResponseEntity.ok(roomService.getParticipantsCount(roomCode));
    }

    @Operation(
            summary = "Get user room history",
            description = "Returns a list of closed rooms created or participated in by the authenticated user."
    )
    @GetMapping("/secure/history")
    public ResponseEntity<List<RoomDTO>> getUserHistory(
            @RequestHeader("X-User-Id") Long userId
    ) throws BadRequestException {
        return ResponseEntity.ok(roomService.getAllClosedRoomsForUser(userId));
    }
}
