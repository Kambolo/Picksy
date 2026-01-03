package com.picksy.userservice.controller;

import com.picksy.userservice.exception.FileUploadException;
import com.picksy.userservice.response.MessageResponse;
import com.picksy.userservice.response.ProfileDTO;
import com.picksy.userservice.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
            summary = "Get user profile by user ID",
            description = "Returns public profile information for a user identified by the given user ID."
    )
    @GetMapping("/public/{id}")
    public ResponseEntity<ProfileDTO> getProfileById(
            @Parameter(description = "Unique user ID") @PathVariable Long id
    ) {
        return ResponseEntity.ok(profileService.findByUserId(id));
    }

    @Operation(
            summary = "Get multiple user profiles",
            description = "Returns profiles for a list of users specified by their user IDs."
    )
    @GetMapping("/public")
    public ResponseEntity<List<ProfileDTO>> getProfilesByUserIds(
            @Parameter(description = "List of user IDs") @RequestParam List<Long> ids
    ) {
        return ResponseEntity.ok(profileService.getProfilesByUserIds(ids));
    }

    @Operation(
            summary = "Update user bio",
            description = "Updates the biography/description of the currently authenticated user."
    )
    @PatchMapping("/secure/bio")
    public ResponseEntity<MessageResponse> changeBio(
            @RequestHeader("X-User-Id") Long userId,
            @Parameter(description = "New bio content") @RequestBody String bio
    ) {
        profileService.changeBio(userId, bio);
        return ResponseEntity.ok(new MessageResponse("Bio updated successfully."));
    }

    @Operation(
            summary = "Update user avatar",
            description = "Uploads and sets a new avatar image for the currently authenticated user."
    )
    @PatchMapping("/secure/avatar")
    public ResponseEntity<MessageResponse> changeAvatar(
            @RequestHeader("X-User-Id") Long userId,
            @Parameter(description = "Avatar image file") @RequestParam MultipartFile image
    ) throws FileUploadException {
        String result = profileService.changeAvatar(image, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse(result));
    }

    @Operation(
            summary = "Delete user avatar",
            description = "Deletes the avatar image of the currently authenticated user."
    )
    @DeleteMapping("/secure/avatar")
    public ResponseEntity<MessageResponse> deleteAvatar(
            @RequestHeader("X-User-Id") Long userId
    ) throws MalformedURLException, FileUploadException {
        profileService.deleteAvatar(userId);
        return ResponseEntity.ok(new MessageResponse("Avatar deleted successfully."));
    }
}
