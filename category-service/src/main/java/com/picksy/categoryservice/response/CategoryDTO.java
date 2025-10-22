package com.picksy.categoryservice.response;

import com.picksy.categoryservice.util.enums.Type;

import java.time.LocalDateTime;

public record CategoryDTO(
        Long id,
        String name,
        Long authorID,
        Type type,
        String photoURL,
        String description,
        int views,
        LocalDateTime created) {
}
