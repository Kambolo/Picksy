package com.picksy.authservice.controller;

import com.picksy.authservice.request.*;
import com.picksy.authservice.response.MessageResponse;
import com.picksy.authservice.response.UserDTO;
import com.picksy.authservice.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<UserDTO> authenticateUser(@RequestBody UserSignInBody user,
                                                    HttpServletResponse response) throws BadRequestException {
        return ResponseEntity.ok(authService.authenticateUser(user, response));
    }

    @PostMapping("/signup")
    public ResponseEntity<MessageResponse> registerUser(@Valid @RequestBody UserSignUpBody user) throws BadRequestException {
        String result = authService.registerUser(user);
        return ResponseEntity.ok(new MessageResponse(result));
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logoutUser(HttpServletResponse response) {
        String result = authService.logoutUser(response);
        return ResponseEntity.ok(new MessageResponse(result));
    }

    @PostMapping("/code/generate")
    public ResponseEntity<MessageResponse> generateCode(@RequestBody EmailRequest emailRequest) throws BadRequestException {
        authService.sendResetPasswordEmail(emailRequest.email());
        return ResponseEntity.ok(new MessageResponse("Email wysłany"));
    }

    @PutMapping("/code/check")
    public ResponseEntity<MessageResponse> checkCode(@RequestBody CheckCodeBody checkCodeBody) throws BadRequestException {
        authService.checkResetCode(checkCodeBody);
        return ResponseEntity.ok(new MessageResponse("Kod poprawny"));
    }

    @PutMapping("/password/reset")
    public ResponseEntity<MessageResponse> resetPassword(@RequestBody ResetPasswordBody resetPasswordBody) throws BadRequestException {
        authService.resetPassword(resetPasswordBody);
        return ResponseEntity.ok(new MessageResponse("Hasło zmienione"));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        UserDTO userDto = authService.loadUserByUsername(username);
        return ResponseEntity.ok(userDto);
    }

}
