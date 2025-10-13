package com.picksy.authservice.request;

public record ResetPasswordBody(String email,
                                String code,
                                String password) {
}
