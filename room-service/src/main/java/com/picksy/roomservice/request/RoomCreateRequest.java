package com.picksy.roomservice.request;

import java.util.List;

public record RoomCreateRequest(Long ownerId,
                                String name,
                                List<Long> categoryIds) {
}
