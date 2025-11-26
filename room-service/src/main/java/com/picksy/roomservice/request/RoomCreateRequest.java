package com.picksy.roomservice.request;

import java.util.List;

public record RoomCreateRequest(
                                String name,
                                List<Long> categoryIds) {
}
