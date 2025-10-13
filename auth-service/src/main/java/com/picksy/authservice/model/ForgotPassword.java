package com.picksy.authservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "forgot_password")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForgotPassword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Kod do resetu hasła
    @Column(nullable = false, unique = true)
    private String resetCode;

    // Czas wygaśnięcia kodu
    @Column(nullable = false)
    private LocalDateTime expirationTime;

    private boolean activated;

    // Relacja do użytkownika
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
}
