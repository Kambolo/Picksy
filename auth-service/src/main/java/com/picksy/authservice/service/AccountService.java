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
            if(userRepository.existsByUsername(newUser.username())){
                throw new BadRequestException("Username taken.");
            }
            oldUser.setUsername(newUser.username());
        }
        userRepository.save(oldUser);
    }
}
