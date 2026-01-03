package com.picksy.authservice.controller;

import com.picksy.authservice.request.*;
import com.picksy.authservice.response.MessageResponse;
import com.picksy.authservice.response.UserDTO;
import com.picksy.authservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "Authenticate user",
            description = "Authenticates the user using email and password and returns user data. A JWT token is set in an HTTP-only cookie.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Authentication successful",
                            content = @Content(schema = @Schema(implementation = UserDTO.class))),
                    @ApiResponse(responseCode = "401", description = "Invalid credentials")
            }
    )
    @PostMapping("/signin")
    public ResponseEntity<UserDTO> authenticateUser(
            @Parameter(description = "User login credentials", required = true)
            @RequestBody UserSignInBody user,
            HttpServletResponse response) {
        return ResponseEntity.ok(authService.authenticateUser(user, response));
    }

    @Operation(
            summary = "Register new user",
            description = "Creates a new user account using the provided registration data.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User registered successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid registration data")
            }
    )
    @PostMapping("/signup")
    public ResponseEntity<MessageResponse> registerUser(
            @Parameter(description = "User registration data", required = true)
            @Valid @RequestBody UserSignUpBody user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse(authService.registerUser(user)));
    }

    @Operation(
            summary = "Logout user",
            description = "Logs out the authenticated user by clearing the JWT cookie.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Logout successful")
            }
    )
    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logoutUser(HttpServletResponse response) {
        return ResponseEntity.ok(new MessageResponse(authService.logoutUser(response)));
    }

    @Operation(
            summary = "Generate password reset code",
            description = "Generates a password reset code and sends it to the user's email address.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Reset code sent"),
                    @ApiResponse(responseCode = "404", description = "Email not found")
            }
    )
    @PostMapping("/code/generate")
    public ResponseEntity<MessageResponse> generateCode(
            @Parameter(description = "Email address", required = true)
            @RequestBody EmailRequest emailRequest) {
        authService.sendResetPasswordEmail(emailRequest.email());
        return ResponseEntity.ok(new MessageResponse("Email has been sent"));
    }

    @Operation(
            summary = "Validate password reset code",
            description = "Validates the provided password reset code for a given email address.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Code is valid"),
                    @ApiResponse(responseCode = "400", description = "Invalid or expired code")
            }
    )
    @PutMapping("/code/check")
    public ResponseEntity<MessageResponse> checkCode(
            @Parameter(description = "Reset code verification data", required = true)
            @RequestBody CheckCodeBody checkCodeBody) {
        authService.checkResetCode(checkCodeBody);
        return ResponseEntity.ok(new MessageResponse("Code is valid"));
    }

    @Operation(
            summary = "Reset password",
            description = "Resets the user's password using a previously validated reset code.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Password reset successful"),
                    @ApiResponse(responseCode = "400", description = "Invalid reset request")
            }
    )
    @PutMapping("/password/reset")
    public ResponseEntity<MessageResponse> resetPassword(
            @Parameter(description = "Password reset data", required = true)
            @RequestBody ResetPasswordBody resetPasswordBody) {
        authService.resetPassword(resetPasswordBody);
        return ResponseEntity.ok(new MessageResponse("Password reset successful"));
    }
}
