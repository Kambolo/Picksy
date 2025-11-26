package com.picksy.userservice.controller;

import com.picksy.userservice.exception.FileUploadException;
import com.picksy.userservice.response.MessageResponse;
import com.picksy.userservice.response.ProfileDTO;
import com.picksy.userservice.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.util.List;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @Operation(
            summary = "Get user profile by ID",
            description = "Fetches the profile details of a user based on their unique user ID."
    )
    @GetMapping("/public/{id}")
    public ResponseEntity<ProfileDTO> getProfileById(
            @Parameter(description = "ID of the user whose profile is requested") @PathVariable Long id
    ) {
        return ResponseEntity.ok(profileService.findByUserId(id));
    }

    @GetMapping("/public")
    public ResponseEntity<List<ProfileDTO>> getProfilesByUserIds(@RequestParam List<Long> ids) {
        return ResponseEntity.ok(profileService.getProfilesByUserIds(ids));
    }

    @Operation(
            summary = "Change user bio",
            description = "Updates the bio/description of the user with the given ID."
    )
    @PatchMapping("/secure/bio")
    public ResponseEntity<MessageResponse> changeBio(
            @RequestHeader("X-User-Id") Long userId,
            @Parameter(description = "New bio content") @RequestBody String bio
    ) {
        profileService.changeBio(userId, bio);
        return ResponseEntity.ok(new MessageResponse("Bio for user id - " + userId + " changed"));
    }

    @Operation(
            summary = "Change user avatar",
            description = "Uploads a new avatar image for the user with the specified ID."
    )
    @PatchMapping("/secure/avatar")
    public ResponseEntity<MessageResponse> changeAvatar(
            @RequestHeader("X-User-Id") Long userId,
            @Parameter(description = "New avatar image file") @RequestParam MultipartFile image
    ) throws FileUploadException {
        String result = profileService.changeAvatar(image, userId);
        return ResponseEntity.ok(new MessageResponse(result));
    }

    @Operation(
            summary = "Delete user avatar",
            description = "Deletes the avatar image associated with the user with the specified ID."
    )
    @DeleteMapping("/secure/avatar")
    public ResponseEntity<MessageResponse> deleteAvatar(
            @RequestHeader("X-User-Id") Long userId,
            @Parameter(description = "ID of the user") @PathVariable Long id
    ) throws MalformedURLException, FileUploadException {
        profileService.deleteAvatar(userId);
        return ResponseEntity.ok(new MessageResponse("Avatar for user id - " + userId + " deleted"));
    }
}
