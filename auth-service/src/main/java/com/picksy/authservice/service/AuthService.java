package com.picksy.authservice.service;

import com.picksy.authservice.Util.CodeGenerator;
import com.picksy.authservice.Util.ROLE;
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
import org.springframework.security.core.userdetails.UserDetails;
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
    private final KafkaTemplate<String, Long> kafkaTemplate;

    public UserDTO authenticateUser(UserSignInBody user, HttpServletResponse response) throws BadRequestException {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.email(),
                            user.password()
                    )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwt = jwtUtils.generateToken(userDetails.getUsername());

            long maxAge = user.rememberMe() ? 7 * 24 * 60 * 60 : -1;

            ResponseCookie cookie = ResponseCookie.from("jwt", jwt)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(maxAge)
                    .sameSite("Strict")
                    .build();


            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            User savedUser = userRepository.findByEmailIgnoreCase(user.email());
            return new UserDTO(savedUser.getId(), user.email(), savedUser.getEmail());

        } catch (org.springframework.security.authentication.BadCredentialsException ex) {
            throw new BadRequestException("Invalid email or password");
        }
    }


    public String registerUser(UserSignUpBody user) throws BadRequestException {
        if (userRepository.existsByUsername(user.username())) {
            throw new BadRequestException("Nazwa użytkownika jest już zajęta.");
        }
        if(userRepository.existsByEmailIgnoreCase(user.email())){
            throw new BadRequestException("Konto z takim adresem email już istnieje.");
        }

        User newUser = User.builder()
                .username(user.username())
                .password(encoder.encode(user.password()))
                .email(user.email())
                .role(ROLE.USER)
                .build();
        User savedUser = userRepository.save(newUser);

        kafkaTemplate.send(TOPIC, savedUser.getId());

        return "User registered successfully!";
    }

    public String logoutUser(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
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
    public void sendResetPasswordEmail(String email) throws BadRequestException {
        if(!userRepository.existsByEmailIgnoreCase(email)){
            throw new BadRequestException("Podany emial nie istnieje w naszej bazie.");
        }

        User user = userRepository.findByEmailIgnoreCase(email);

        String resetCode = CodeGenerator.generateResetCode();

        ForgotPassword forgotPassword = forgotPasswordRepository.findByUser(user);

        if(forgotPassword == null) {
            forgotPasswordRepository.save(ForgotPassword.builder()
                    .user(user)
                    .resetCode(resetCode)
                    .expirationTime(LocalDateTime.now().plusMinutes(10))
                    .activated(false)
                    .build());
        } else{
            forgotPassword.setResetCode(resetCode);
            forgotPassword.setExpirationTime(LocalDateTime.now().plusMinutes(10));
            forgotPasswordRepository.save(forgotPassword);
        }


        String msgBody = "Your reset code: " + resetCode;

        EmailDetails emailDetails = new EmailDetails(
                email,
                msgBody,
                "Password reset");

        emailService.sendSimpleMail(emailDetails);
    }

    @Transactional
    public void checkResetCode(CheckCodeBody checkCodeBody) throws BadRequestException {
        if(!userRepository.existsByEmailIgnoreCase(checkCodeBody.email())){
            throw new BadRequestException("Podany email nie istnieje w naszej bazie.");
        }

        User user = userRepository.findByEmailIgnoreCase(checkCodeBody.email());

        if(!forgotPasswordRepository.existsByUser(user)){
            throw new BadRequestException("Kod nie został wygenerowany");
        }

        if(forgotPasswordRepository.findByResetCodeAndUser(checkCodeBody.code(), user)==null){
            throw new BadRequestException("Niepoprawny kod resetu");
        }

        ForgotPassword forgotPassword = forgotPasswordRepository.findByResetCodeAndUser(checkCodeBody.code(), user);

        if(LocalDateTime.now().isAfter(forgotPassword.getExpirationTime())){
            throw new BadRequestException("Kod resetu utracił ważność");
        }

        forgotPassword.setActivated(true);
        forgotPasswordRepository.save(forgotPassword);
    }

    @Transactional
    public void resetPassword(ResetPasswordBody resetPasswordBody) throws BadRequestException {
        if(!userRepository.existsByEmailIgnoreCase(resetPasswordBody.email())){
            throw new BadRequestException("Podany email nie istnieje w naszej bazie.");
        }

        User user = userRepository.findByEmailIgnoreCase(resetPasswordBody.email());

        if(!forgotPasswordRepository.existsByUser(user)){
            throw new BadRequestException("Kod nie został wygenerowany");
        }

        ForgotPassword forgotPassword = forgotPasswordRepository.findByResetCodeAndUser(resetPasswordBody.code(), user);

        if(!forgotPassword.isActivated()){
            throw new BadRequestException("Błąd podaczas resetowania hasła");
        }

        forgotPassword.setActivated(false);
        forgotPasswordRepository.save(forgotPassword);

        user.setPassword(encoder.encode(resetPasswordBody.password()));
        userRepository.save(user);
    }
}
