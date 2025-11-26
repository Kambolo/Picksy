package com.picksy.authservice.request;

import java.time.LocalDateTime;

public record BanUserBody(Long userId, LocalDateTime banDate) {}
