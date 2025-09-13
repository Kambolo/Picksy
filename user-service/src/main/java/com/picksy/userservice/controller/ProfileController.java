package com.picksy.userservice.controller;

import com.picksy.userservice.exception.FileUploadException;
import com.picksy.userservice.response.ProfileDTO;
import com.picksy.userservice.service.ProfileService;
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

    @GetMapping("/public/{id}")
    public ResponseEntity<ProfileDTO> getProfileById(@PathVariable Long id){
        return ResponseEntity.ok().body(profileService.findByUserId(id));
    }

    @PostMapping("/secure/{id}/bio")
    public ResponseEntity<String> changeBio(@PathVariable Long id, @RequestBody String bio){
        profileService.changeBio(id, bio);
        return ResponseEntity.ok().body("Bio for user id - " + id + " changed");
    }

    @PostMapping("/secure/{id}/avatar")
    public ResponseEntity<String> changeAvatar(@PathVariable Long id, @RequestParam MultipartFile image) throws FileUploadException {
        profileService.changeAvatar(image ,id);
        return ResponseEntity.ok().body("Avatar for user id - " + id + " changed");
    }

    @DeleteMapping("/secure/{id}/avatar")
    public ResponseEntity<String> deleteAvatar(@PathVariable Long id) throws MalformedURLException, FileUploadException {
        profileService.deleteAvatar(id);
        return ResponseEntity.ok().body("Avatar for user id - " + id + " deleted");
    }
}
