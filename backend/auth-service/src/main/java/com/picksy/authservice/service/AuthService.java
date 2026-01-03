package com.picksy.authservice.service;

import com.picksy.UserProfileMessage;
import com.picksy.authservice.Util.CodeGenerator;
import com.picksy.authservice.Util.ROLE;
import com.picksy.authservice.exception.InvalidRequestException;
import com.picksy.authservice.exception.UserAlreadyExistsException;
import com.picksy.authservice.exception.UserBannedException;
import com.picksy.authservice.exception.UserNotFoundException;
import com.picksy.authservice.model.ForgotPassword;
import com.picksy.authservice.repository.ForgotPasswordRepository;
import com.picksy.authservice.repository.UserRepository;
import com.picksy.authservice.Util.JwtUtil;
import com.picksy.authservice.model.User;
import com.picksy.authservice.request.CheckCodeBody;
import com.picksy.authservice.request.ResetPasswordBody;
import com.picksy.authservice.response.EmailDetails;
import com.picksy.authservice.request.UserSignInBody;
import com.picksy.authservice.request.UserSignUpBody;
import com.picksy.authservice.response.UserDTO;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final AuthenticationManager authenticationManager;
  private final UserRepository userRepository;
  private final PasswordEncoder encoder;
  private final JwtUtil jwtUtils;
  private final EmailService emailService;
  private final ForgotPasswordRepository forgotPasswordRepository;

  private static final String TOPIC = "register-user";
  private final KafkaTemplate<String, UserProfileMessage> kafkaTemplate;

  public UserDTO authenticateUser(UserSignInBody user, HttpServletResponse response) {
    User savedUser = userRepository.findByEmailIgnoreCase(user.email());
    if (savedUser == null) {
      throw new UserNotFoundException("Invalid email or password");
    }

    if (savedUser.getIsBanned()) {
      if (savedUser.getBannedUntil() == LocalDateTime.MAX) {
        throw new UserBannedException("User is banned until: forever");
      }
      // If ban time passed unban user
      if (savedUser.getBannedUntil().isBefore(LocalDateTime.now())) {
        savedUser.setIsBanned(false);
      } else {
        throw new UserBannedException("User is banned until:" + savedUser.getBannedUntil());
      }
    }

    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(user.email(), user.password()));
    } catch (org.springframework.security.authentication.BadCredentialsException ex) {
      throw new InvalidRequestException("Invalid email or password");
    }

    String jwt =
        jwtUtils.generateToken(
            savedUser.getId(), savedUser.getEmail(), savedUser.getRole().toString());

    long maxAge = user.rememberMe() ? 24 * 60 * 60 : -1;

    ResponseCookie cookie =
        ResponseCookie.from("jwt", jwt)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(maxAge)
            .sameSite("Strict")
            .build();

    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

    return new UserDTO(
        savedUser.getId(), savedUser.getUsername(), savedUser.getEmail(), savedUser.getRole(), savedUser.getIsBanned());
  }

  @Transactional
  public String registerUser(UserSignUpBody user) {

    if (userRepository.existsByUsernameIgnoreCase(user.username())) {
      throw new UserAlreadyExistsException("Username already exists.");
    }

    if (userRepository.existsByEmailIgnoreCase(user.email())) {
      throw new UserAlreadyExistsException("Email already exists.");
    }

    User newUser =
        User.builder()
            .username(user.username())
            .password(encoder.encode(user.password()))
            .email(user.email())
            .role(ROLE.USER)
            .isBanned(false)
            .bannedUntil(null)
            .build();

    User savedUser = userRepository.save(newUser);

    kafkaTemplate.send(TOPIC, new UserProfileMessage(savedUser.getId(), null));

    return "User registered successfully!";
  }

  public String logoutUser(HttpServletResponse response) {
    ResponseCookie cookie =
        ResponseCookie.from("jwt", "")
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(0)
            .sameSite("Strict")
            .build();

    response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    return "Logged out successfully";
  }

  @Transactional
  public void sendResetPasswordEmail(String email) {

    User user = userRepository.findByEmailIgnoreCase(email);
    if (user == null) {
      throw new UserNotFoundException("Email not found.");
    }

    String resetCode = CodeGenerator.generateResetCode();

    ForgotPassword forgotPassword = forgotPasswordRepository.findByUser(user);

    if (forgotPassword == null) {
      forgotPassword =
          ForgotPassword.builder()
              .user(user)
              .resetCode(resetCode)
              .expirationTime(LocalDateTime.now().plusMinutes(10))
              .activated(false)
              .build();
    } else {
      forgotPassword.setResetCode(resetCode);
      forgotPassword.setExpirationTime(LocalDateTime.now().plusMinutes(10));
    }

    forgotPasswordRepository.save(forgotPassword);

    emailService.sendSimpleMail(
        new EmailDetails(email, "Your reset code: " + resetCode, "Password reset"));
  }

  @Transactional
  public void checkResetCode(CheckCodeBody body) {
    User user = userRepository.findByEmailIgnoreCase(body.email());
    ForgotPassword forgotPassword = checkCode(user, body.code());

    if (LocalDateTime.now().isAfter(forgotPassword.getExpirationTime())) {
      throw new InvalidRequestException("Code has expired");
    }

    forgotPassword.setActivated(true);
    forgotPasswordRepository.save(forgotPassword);
  }

  @Transactional
  public void resetPassword(ResetPasswordBody body) {
    User user = userRepository.findByEmailIgnoreCase(body.email());
    ForgotPassword forgotPassword = checkCode(user, body.code());

    if (!forgotPassword.isActivated()) {
      throw new InvalidRequestException("Kod nie został zatwierdzony");
    }

    forgotPassword.setActivated(false);
    forgotPasswordRepository.save(forgotPassword);

    user.setPassword(encoder.encode(body.password()));
    userRepository.save(user);
  }

  private ForgotPassword checkCode(User user, String code) {
    if (user == null) {
      throw new UserNotFoundException("Email not found.");
    }

    ForgotPassword forgotPassword = forgotPasswordRepository.findByUser(user);
    if (forgotPassword == null) {
      throw new InvalidRequestException("Code does not exist.");
    }

    if (!forgotPassword.getResetCode().equals(code)) {
      throw new InvalidRequestException("Bad code");
    }

    return forgotPassword;
  }
}
