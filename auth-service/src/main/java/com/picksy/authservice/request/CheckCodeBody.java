package com.picksy.authservice.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CheckCodeBody(
                            @NotBlank
                            @Email(message = "Invalid email format")
                            String email,
                            @NotBlank
                            String code) {
}
