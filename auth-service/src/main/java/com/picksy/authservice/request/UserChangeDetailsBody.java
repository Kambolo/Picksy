package com.picksy.authservice.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record UserChangeDetailsBody(
                    @NotNull Long id,
                    String username,
                    @Email(message = "Zły format emailu") String email
) {
}
