package com.picksy.roomservice.response;

import java.util.List;
import java.util.Map;

public record RoomDTO(String roomCode,
                      String name,
                      List<Long> categoryIds,
                      boolean voting_started,
                      boolean room_closed,
                      Map<Long, String> participants) {
}
