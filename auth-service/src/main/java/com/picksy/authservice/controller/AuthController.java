package com.picksy.authservice.controller;

import com.picksy.authservice.request.UserSignInBody;
import com.picksy.authservice.request.UserSignUpBody;
import com.picksy.authservice.response.UserDTO;
import com.picksy.authservice.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<UserDTO> authenticateUser(@RequestBody UserSignInBody user,
                                                    HttpServletResponse response) {
       return ResponseEntity.ok().body(authService.authenticateUser(user, response));
    }

    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@RequestBody UserSignUpBody user) throws BadRequestException {
        return ResponseEntity.ok().body(authService.registerUser(user));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(HttpServletResponse response) {
        return ResponseEntity.ok().body(authService.logoutUser(response));
    }

}