package com.picksy.roomservice.response;

public record RoomDTO(String roomCode,
                      String name,
                      Long ownerId,
                      boolean voting_started) {
}
