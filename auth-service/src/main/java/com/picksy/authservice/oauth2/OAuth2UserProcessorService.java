package com.picksy.authservice.oauth2;

import com.picksy.UserProfileMessage;
import com.picksy.authservice.Util.JwtUtil;
import com.picksy.authservice.Util.ROLE;
import com.picksy.authservice.model.User;
import com.picksy.authservice.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OAuth2UserProcessorService {
  private final UserRepository userRepository;

  private final PasswordEncoder encoder;
  private final JwtUtil jwtUtils;

  private final KafkaTemplate<String, UserProfileMessage> kafkaTemplate;

  @Transactional
  public void processOAuthPostLogin(Map<String, Object> attributes, HttpServletResponse response)
      throws BadRequestException {
    String email = (String) attributes.get("email");
    String picture = (String) attributes.get("picture");

    if (email == null) return;

    User existing = userRepository.findByEmailIgnoreCase(email);

    if (existing == null) {
      existing =
          userRepository.save(
              User.builder()
                  .email(email)
                  .username(email.substring(0, email.indexOf("@")))
                  .password(encoder.encode(UUID.randomUUID().toString()))
                  .role(ROLE.USER)
                  .isBanned(false)
                  .bannedUntil(null)
                  .build());

      kafkaTemplate.send("register-user", new UserProfileMessage(existing.getId(), picture));
    }

    // generowanie JWT bez AuthenticationManager
    String jwt =
        jwtUtils.generateToken(
            existing.getId(), existing.getEmail(), existing.getRole().toString());

    ResponseCookie cookie =
        ResponseCookie.from("jwt", jwt)
            .httpOnly(true)
            .secure(true)
            .sameSite("Strict")
            .path("/")
            .maxAge(24 * 60 * 60)
            .build();

    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
  }
}
