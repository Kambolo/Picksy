package com.picksy.categoryservice.response;

import com.picksy.categoryservice.util.enums.Type;

public record CategoryDTO(
                    Long id,
                    String name,
                    Long authorID,
                    Type type,
                    String photoURL,
                    String description) {
}
