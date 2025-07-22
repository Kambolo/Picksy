package com.picksy.authservice.repository;

import com.picksy.authservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    boolean existsByUsername(String username);
    User findByEmail(String email);
    boolean existsByEmail(String email);
}
