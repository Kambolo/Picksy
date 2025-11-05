package com.picksy.userservice.controller;

import com.picksy.userservice.exception.FileUploadException;
import com.picksy.userservice.response.MessageResponse;
import com.picksy.userservice.response.ProfileDTO;
import com.picksy.userservice.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;

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

    @Operation(
            summary = "Change user bio",
            description = "Updates the bio/description of the user with the given ID."
    )
    @PatchMapping("/secure/{id}/bio")
    public ResponseEntity<MessageResponse> changeBio(
            @Parameter(description = "ID of the user") @PathVariable Long id,
            @Parameter(description = "New bio content") @RequestBody String bio
    ) {
        profileService.changeBio(id, bio);
        return ResponseEntity.ok(new MessageResponse("Bio for user id - " + id + " changed"));
    }

    @Operation(
            summary = "Change user avatar",
            description = "Uploads a new avatar image for the user with the specified ID."
    )
    @PatchMapping("/secure/{id}/avatar")
    public ResponseEntity<MessageResponse> changeAvatar(
            @Parameter(description = "ID of the user") @PathVariable Long id,
            @Parameter(description = "New avatar image file") @RequestParam MultipartFile image
    ) throws FileUploadException {
        String result = profileService.changeAvatar(image, id);
        return ResponseEntity.ok(new MessageResponse(result));
    }

    @Operation(
            summary = "Delete user avatar",
            description = "Deletes the avatar image associated with the user with the specified ID."
    )
    @DeleteMapping("/secure/{id}/avatar")
    public ResponseEntity<MessageResponse> deleteAvatar(
            @Parameter(description = "ID of the user") @PathVariable Long id
    ) throws MalformedURLException, FileUploadException {
        profileService.deleteAvatar(id);
        return ResponseEntity.ok(new MessageResponse("Avatar for user id - " + id + " deleted"));
    }
}
