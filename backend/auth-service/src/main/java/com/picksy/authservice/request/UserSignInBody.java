package com.picksy.authservice.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserSignInBody(@NotBlank String email,
                             @NotBlank String password,
                             boolean rememberMe) {
}
