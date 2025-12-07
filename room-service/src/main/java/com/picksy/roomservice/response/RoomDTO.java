package com.picksy.roomservice.response;

import com.picksy.roomservice.model.SetCategoryKey;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record RoomDTO(String roomCode,
                      String name,
                      List<SetCategoryKey> categories,
                      boolean votingStarted,
                      boolean roomClosed,
                      Map<Long, String> participants,
                      Long ownerId,
                      LocalDateTime createdAt) {
}
