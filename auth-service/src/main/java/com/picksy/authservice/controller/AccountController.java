package com.picksy.authservice.controller;

import com.picksy.authservice.request.UserChangeDetailsBody;
import com.picksy.authservice.response.UserDTO;
import com.picksy.authservice.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/account")
@RequiredArgsConstructor
public class AccountController {
    final private AccountService accountService;

    @GetMapping("/secure/me")
    public ResponseEntity<UserDTO> getCurrentUser(@RequestHeader("X-User-Id") Long id) {
        UserDTO userDto = accountService.loadUserByID(id);
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        UserDTO userDto = accountService.loadUserByID(id);
        return ResponseEntity.ok(userDto);
    }

    @PatchMapping("/details")
    public ResponseEntity<String> changeAccountDetails(@Valid @RequestBody UserChangeDetailsBody newUser){
        accountService.changeAccDetails(newUser);
        return ResponseEntity.ok("User credentials changed.");
    }

    @GetMapping("/search")
    public ResponseEntity<Page<UserDTO>> searchUserByUsername(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending,
            @RequestParam String username
    ) {
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.status(HttpStatus.OK).body(accountService.searchByUsername(username, pageable));
    }
}
