package com.picksy.userservice.response;

public record ProfileDTO(Long userId,
                         String avatarUrl,
                         String bio) {
}
