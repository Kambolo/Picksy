package com.picksy.roomservice.request;

import com.picksy.roomservice.model.SetCategoryKey;

import java.util.List;

public record RoomCreateRequest(
                                String name,
                                List<SetCategoryKey> categories) {
}
