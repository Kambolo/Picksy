package com.picksy.categoryservice.response;

import com.picksy.categoryservice.util.enums.Type;

public record CategoryDTO(
                    Long id,
                    String name,
                    String author,
                    Type type,
                    String photoURL) {
}
