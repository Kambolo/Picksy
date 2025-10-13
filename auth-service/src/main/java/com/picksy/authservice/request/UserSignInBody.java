package com.picksy.authservice.request;

public record UserSignInBody(String email,
                             String password,
                             boolean rememberMe) {
}
