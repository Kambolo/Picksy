package com.picksy.authservice.service;

import com.picksy.authservice.model.User;
import com.picksy.authservice.repository.UserRepository;
import com.picksy.authservice.request.UserChangeDetailsBody;
import com.picksy.authservice.response.UserDTO;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class AccountService {
    final private UserRepository userRepository;

    public Page<UserDTO> searchByUsername(String username, Pageable pageable){
        return userRepository.findByUsernameContainingIgnoreCase(username, pageable)
                .map(user -> new UserDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail()
                ));
    }

    @Transactional
    public void changeAccDetails(UserChangeDetailsBody newUser){
        User oldUser = userRepository.findById(newUser.id()).orElseThrow(() -> new BadRequestException("User not found."));
        if(!newUser.username().isBlank()){
            if(!Objects.equals(oldUser.getUsername(), newUser.username()) && userRepository.existsByUsernameIgnoreCase(newUser.username())){
                throw new BadRequestException("Nazwa użytkownika jest zajęta.");
            }
            oldUser.setUsername(newUser.username());
        }
        if(!Objects.equals(oldUser.getEmail(), newUser.email()) && !newUser.email().isBlank()){
            if(userRepository.existsByEmailIgnoreCase(newUser.email())){
                throw new BadRequestException("Email jest zajęty.");
            }
            oldUser.setEmail(newUser.email());
        }

        userRepository.save(oldUser);
    }

    public UserDTO loadUserByID(Long id) {
        Optional<User> savedUser = userRepository.findById(id);
        if (!savedUser.isPresent()) {
            throw new RuntimeException("User not found");
        }
        return new UserDTO(savedUser.get().getId(), savedUser.get().getUsername(), savedUser.get().getEmail());
    }

    public UserDTO loadUserByEmail(String email) {
        User savedUser = userRepository.findByEmailIgnoreCase(email);
        if (savedUser == null) {
            throw new RuntimeException("User not found");
        }
        return new UserDTO(savedUser.getId(), savedUser.getUsername(), savedUser.getEmail());
    }
}
