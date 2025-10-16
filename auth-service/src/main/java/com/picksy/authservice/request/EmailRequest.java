package com.picksy.authservice.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailRequest(@NotBlank
                           @Email(message = "Invalid email format")String email) {
}
