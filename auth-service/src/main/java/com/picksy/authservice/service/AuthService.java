package com.picksy.authservice.service;

import com.picksy.authservice.Util.ROLE;
import com.picksy.authservice.repository.UserRepository;
import com.picksy.authservice.Util.JwtUtil;
import com.picksy.authservice.model.User;
import com.picksy.authservice.request.UserSignInBody;
import com.picksy.authservice.request.UserSignUpBody;
import com.picksy.authservice.response.UserDTO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtils;

    public UserDTO authenticateUser(UserSignInBody user,  HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.username(),
                        user.password()
                )
        );
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwt = jwtUtils.generateToken(userDetails.getUsername());

        ResponseCookie cookie = ResponseCookie.from("jwt", jwt)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(24 * 60 * 60) // 1 dzień
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        User savedUser = userRepository.findByUsername(user.username());
        return new UserDTO(savedUser.getId(), user.username(), savedUser.getEmail());
    }

    public String registerUser(UserSignUpBody user) throws BadRequestException {
        if (userRepository.existsByUsername(user.username())) {
            throw new BadRequestException("Username already exists");
        }
        if(userRepository.existsByEmail(user.email())){
            throw new BadRequestException("Email already exists");
        }

        User newUser = new User(
                null,
                user.username(),
                encoder.encode(user.password()),
                user.email(),
                ROLE.USER
        );
        userRepository.save(newUser);
        return "User registered successfully!";
    }

    public String logoutUser(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0) // 0 dni, usuwamy ciasteczko
                .sameSite("Strict")
                .build();

        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return "Logged out successfully";
    }

}
