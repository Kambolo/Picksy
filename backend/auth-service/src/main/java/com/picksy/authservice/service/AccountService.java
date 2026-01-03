package com.picksy.authservice.service;

import com.picksy.authservice.exception.ForbiddenOperationException;
import com.picksy.authservice.exception.InvalidRequestException;
import com.picksy.authservice.exception.UserAlreadyExistsException;
import com.picksy.authservice.exception.UserNotFoundException;
import com.picksy.authservice.model.User;
import com.picksy.authservice.repository.UserRepository;
import com.picksy.authservice.request.BanUserBody;
import com.picksy.authservice.request.UserChangeDetailsBody;
import com.picksy.authservice.response.UserDTO;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {
  private final UserRepository userRepository;

  public Page<UserDTO> searchByUsername(String username, Pageable pageable) {
    return userRepository
        .findByUsernameContainingIgnoreCase(username, pageable)
        .map(this::userToDTO);
  }

  @Transactional
  public void changeAccDetails(Long id, UserChangeDetailsBody newUser) {
    User oldUser =
        userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));

    if (!newUser.username().isBlank()) {
      if (!Objects.equals(oldUser.getUsername(), newUser.username())
          && userRepository.existsByUsernameIgnoreCase(newUser.username())) {
        throw new UserAlreadyExistsException("Username is already taken");
      }
      oldUser.setUsername(newUser.username());
    }

    if (!newUser.email().isBlank() && !Objects.equals(oldUser.getEmail(), newUser.email())) {

      if (userRepository.existsByEmailIgnoreCase(newUser.email())) {
        throw new UserAlreadyExistsException("Email is already taken");
      }
      oldUser.setEmail(newUser.email());
    }

    userRepository.save(oldUser);
  }

  public UserDTO loadUserByID(Long id) {
    User user =
        userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
    return userToDTO(user);
  }

  public UserDTO loadUserByEmail(String email) {
    User user = userRepository.findByEmailIgnoreCase(email);
    if (user == null) {
      throw new UserNotFoundException("User not found");
    }
    return userToDTO(user);
  }

  public Page<UserDTO> findAll(Pageable pageable) {
    return userRepository.findAll(pageable).map(this::userToDTO);
  }

  private UserDTO userToDTO(User user) {
    return new UserDTO(
        user.getId(), user.getUsername(), user.getEmail(), user.getRole(), user.getIsBanned());
  }

  @Transactional
  public void banUser(String role, BanUserBody body) {

    if (!role.equalsIgnoreCase("ADMIN")) {
      throw new ForbiddenOperationException("Only admin can ban users");
    }

    User user =
        userRepository
            .findById(body.userId())
            .orElseThrow(() -> new UserNotFoundException("User not found"));

    if (body.banDate() == null) {
      user.setBannedUntil(LocalDateTime.MAX);
    }else{
        user.setBannedUntil(body.banDate());
    }
    user.setIsBanned(true);

    userRepository.save(user);
  }

  @Transactional
  public void unbanUser(String role, Long userId) {
    if (!role.equalsIgnoreCase("ADMIN")) {
      throw new ForbiddenOperationException("Only admin can unban users");
    }

    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found"));

    user.setIsBanned(false);

    userRepository.save(user);
  }
}
