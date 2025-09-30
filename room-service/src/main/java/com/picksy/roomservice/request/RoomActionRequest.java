package com.picksy.roomservice.request;

public record RoomActionRequest(Long ownerId,
                                String roomCode) {
}
