package com.picksy.authservice.controller;

import com.picksy.authservice.request.*;
import com.picksy.authservice.response.MessageResponse;
import com.picksy.authservice.response.UserDTO;
import com.picksy.authservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "Authenticate user",
            description = "Authenticates a user with provided email and password. Returns user data and sets authentication tokens in the response headers."
    )
    @PostMapping("/signin")
    public ResponseEntity<UserDTO> authenticateUser(
            @Parameter(description = "User credentials for sign-in") @RequestBody UserSignInBody user,
            HttpServletResponse response
    ) throws BadRequestException {
        return ResponseEntity.ok(authService.authenticateUser(user, response));
    }

    @Operation(
            summary = "Register a new user",
            description = "Registers a new user in the system with provided details (email, password, username, etc.)."
    )
    @PostMapping("/signup")
    public ResponseEntity<MessageResponse> registerUser(
            @Parameter(description = "User registration data") @Valid @RequestBody UserSignUpBody user
    ) throws BadRequestException {
        String result = authService.registerUser(user);
        return ResponseEntity.ok(new MessageResponse(result));
    }

    @Operation(
            summary = "Logout user",
            description = "Logs out the currently authenticated user and clears authentication tokens."
    )
    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logoutUser(HttpServletResponse response) {
        String result = authService.logoutUser(response);
        return ResponseEntity.ok(new MessageResponse(result));
    }

    @Operation(
            summary = "Generate password reset code",
            description = "Generates and sends a password reset code to the provided email address."
    )
    @PostMapping("/code/generate")
    public ResponseEntity<MessageResponse> generateCode(
            @Parameter(description = "Email address to send the reset code") @RequestBody EmailRequest emailRequest
    ) throws BadRequestException {
        authService.sendResetPasswordEmail(emailRequest.email());
        return ResponseEntity.ok(new MessageResponse("Email wysłany"));
    }

    @Operation(
            summary = "Check password reset code",
            description = "Verifies whether the provided password reset code is valid."
    )
    @PutMapping("/code/check")
    public ResponseEntity<MessageResponse> checkCode(
            @Parameter(description = "Reset code and associated email") @RequestBody CheckCodeBody checkCodeBody
    ) throws BadRequestException {
        authService.checkResetCode(checkCodeBody);
        return ResponseEntity.ok(new MessageResponse("Kod poprawny"));
    }

    @Operation(
            summary = "Reset user password",
            description = "Resets the user's password using a valid reset code."
    )
    @PutMapping("/password/reset")
    public ResponseEntity<MessageResponse> resetPassword(
            @Parameter(description = "Password reset data including code and new password") @RequestBody ResetPasswordBody resetPasswordBody
    ) throws BadRequestException {
        authService.resetPassword(resetPasswordBody);
        return ResponseEntity.ok(new MessageResponse("Hasło zmienione"));
    }
}
