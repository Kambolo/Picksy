package com.picksy.authservice.model;

import com.picksy.authservice.Util.ROLE;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "users")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    @Column(unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private ROLE role;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private ForgotPassword forgotPassword;

    @Column(nullable = false)
    private Boolean isBanned;

    private LocalDateTime bannedUntil;

    public void setForgotPassword(ForgotPassword forgotPassword) {
        this.forgotPassword = forgotPassword;
        if (forgotPassword != null && forgotPassword.getUser() != this) {
            forgotPassword.setUser(this);
        }
    }
}
