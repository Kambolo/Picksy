package com.picksy.authservice.controller;

import com.picksy.authservice.request.BanUserBody;
import com.picksy.authservice.request.UserChangeDetailsBody;
import com.picksy.authservice.response.UserDTO;
import com.picksy.authservice.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @Operation(
            summary = "Get current authenticated user",
            description = "Returns details of the currently authenticated user based on the identifier provided by the API Gateway.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User data returned successfully",
                            content = @Content(schema = @Schema(implementation = UserDTO.class))),
                    @ApiResponse(responseCode = "401", description = "User not authenticated"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @GetMapping("/secure/me")
    public ResponseEntity<UserDTO> getCurrentUser(
            @Parameter(description = "Authenticated user identifier", required = true)
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(accountService.loadUserByID(userId));
    }

    @Operation(
            summary = "Get user profile by ID",
            description = "Returns profile information of a user identified by their unique ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User profile returned",
                            content = @Content(schema = @Schema(implementation = UserDTO.class))),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @GetMapping("/public/user/{id}")
    public ResponseEntity<UserDTO> getUser(
            @Parameter(description = "User identifier", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(accountService.loadUserByID(id));
    }

    @Operation(
            summary = "Update account details",
            description = "Allows an authenticated user to update editable account data such as username or description.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Account details updated"),
                    @ApiResponse(responseCode = "400", description = "Invalid request data"),
                    @ApiResponse(responseCode = "401", description = "User not authenticated")
            }
    )
    @PatchMapping("/secure/details")
    public ResponseEntity<String> changeAccountDetails(
            @Parameter(description = "Authenticated user identifier", required = true)
            @RequestHeader("X-User-Id") Long userId,
            @Parameter(description = "New account details", required = true)
            @Valid @RequestBody UserChangeDetailsBody newUser) {
        accountService.changeAccDetails(userId, newUser);
        return ResponseEntity.ok("User credentials changed.");
    }

    @Operation(
            summary = "Search users by username",
            description = "Returns a paginated list of users whose usernames match the provided search pattern.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Search results returned")
            }
    )
    @GetMapping("/public/search")
    public ResponseEntity<Page<UserDTO>> searchUserByUsername(
            @Parameter(description = "Page number")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "5") int size,
            @Parameter(description = "Sort field")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort ascending")
            @RequestParam(defaultValue = "true") boolean ascending,
            @Parameter(description = "Username search pattern", required = true)
            @RequestParam String pattern) {

        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(accountService.searchByUsername(pattern, pageable));
    }

    @Operation(
            summary = "Get all users profiles",
            description = "Returns a paginated list of all user profiles.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Profiles returned successfully")
            }
    )
    @GetMapping("/public")
    public ResponseEntity<Page<UserDTO>> getAllProfiles(
            @Parameter(description = "Page number")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "5") int size,
            @Parameter(description = "Sort field")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort ascending")
            @RequestParam(defaultValue = "true") boolean ascending) {

        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(accountService.findAll(pageable));
    }

    @Operation(
            summary = "Ban user",
            description = "Allows an administrator to ban a user for a specified period or permanently.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User banned successfully"),
                    @ApiResponse(responseCode = "403", description = "Insufficient permissions")
            }
    )
    @PatchMapping("/secure/ban")
    public ResponseEntity<String> banUser(
            @Parameter(description = "Role of the authenticated user", required = true)
            @RequestHeader("X-User-Role") String role,
            @Parameter(description = "Ban request data", required = true)
            @RequestBody BanUserBody banUser) {
        accountService.banUser(role, banUser);
        return ResponseEntity.ok("User ban successful.");
    }

    @Operation(
            summary = "Unban user",
            description = "Allows an administrator to remove a ban from a user account.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User unbanned successfully"),
                    @ApiResponse(responseCode = "403", description = "Insufficient permissions")
            }
    )
    @PatchMapping("/secure/{userId}/unban")
    public ResponseEntity<String> unbanUser(
            @Parameter(description = "Role of the authenticated user", required = true)
            @RequestHeader("X-User-Role") String role,
            @Parameter(description = "User identifier", required = true)
            @PathVariable Long userId) {
        accountService.unbanUser(role, userId);
        return ResponseEntity.ok("User unblocked successful.");
    }
}
