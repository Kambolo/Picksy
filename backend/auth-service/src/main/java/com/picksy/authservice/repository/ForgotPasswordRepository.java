package com.picksy.authservice.repository;

import com.picksy.authservice.model.ForgotPassword;
import com.picksy.authservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ForgotPasswordRepository extends JpaRepository<ForgotPassword, Long> {
    boolean existsByUser(User user);
    ForgotPassword findByResetCodeAndUser(String resetCode, User user);
    ForgotPassword findByUser(User user);
}
