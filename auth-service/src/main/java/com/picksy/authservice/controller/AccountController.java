package com.picksy.authservice.controller;

import com.picksy.authservice.request.UserChangeDetailsBody;
import com.picksy.authservice.response.UserDTO;
import com.picksy.authservice.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @Operation(
            summary = "Get current user details",
            description = "Fetches the details of the currently authenticated user based on the X-User-Id header."
    )
    @GetMapping("/secure/me")
    public ResponseEntity<UserDTO> getCurrentUser(
            @Parameter(description = "ID of the authenticated user (from gateway header)") @RequestHeader("X-User-Id") Long id
    ) {
        UserDTO userDto = accountService.loadUserByID(id);
        return ResponseEntity.ok(userDto);
    }

    @Operation(
            summary = "Get user details by ID",
            description = "Fetches the details of a user given their unique user ID."
    )
    @GetMapping("/user/{id}")
    public ResponseEntity<UserDTO> getUser(
            @Parameter(description = "ID of the user to retrieve") @PathVariable Long id
    ) {
        UserDTO userDto = accountService.loadUserByID(id);
        return ResponseEntity.ok(userDto);
    }

    @Operation(
            summary = "Change account details",
            description = "Updates the current user's account information such as username, email, or other editable fields."
    )
    @PatchMapping("/details")
    public ResponseEntity<String> changeAccountDetails(
            @Parameter(description = "New user account details") @Valid @RequestBody UserChangeDetailsBody newUser
    ) {
        accountService.changeAccDetails(newUser);
        return ResponseEntity.ok("User credentials changed.");
    }

    @Operation(
            summary = "Search users by username",
            description = "Searches for users whose username matches the provided pattern, with optional pagination and sorting."
    )
    @GetMapping("/search")
    public ResponseEntity<Page<UserDTO>> searchUserByUsername(
            @Parameter(description = "Page number (default is 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size (default is 5)") @RequestParam(defaultValue = "5") int size,
            @Parameter(description = "Field to sort by (default is 'id')") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort ascending? (default is true)") @RequestParam(defaultValue = "true") boolean ascending,
            @Parameter(description = "Username pattern to search") @RequestParam String username
    ) {
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(accountService.searchByUsername(username, pageable));
    }
}
