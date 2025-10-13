package com.picksy.authservice.request;

public record CheckCodeBody(String email,
                            String code) {
}
