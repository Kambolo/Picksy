package com.picksy.categoryservice.response;

import java.time.LocalDateTime;
import java.util.List;

public record CategorySetDTO(
    Long id,
    String name,
    List<CategoryDTO> categories,
    Long authorId,
    int views,
    LocalDateTime created,
    Boolean isPublic) {}
