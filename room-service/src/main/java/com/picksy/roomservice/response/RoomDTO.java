package com.picksy.roomservice.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record RoomDTO(String roomCode,
                      String name,
                      List<Long> categoryIds,
                      boolean votingStarted,
                      boolean roomClosed,
                      Map<Long, String> participants,
                      Long ownerId,
                      LocalDateTime createdAt) {
}
