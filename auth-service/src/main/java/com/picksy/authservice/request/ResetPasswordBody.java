package com.picksy.authservice.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ResetPasswordBody(@NotBlank
                                @Email(message = "Invalid email format")String email,
                                @NotBlank String code,
                                @NotBlank
                                @Pattern(
                                        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                                        message = "Password must be at least 8 characters long and include uppercase, lowercase, number, and special character"
                                )String password) {
}
