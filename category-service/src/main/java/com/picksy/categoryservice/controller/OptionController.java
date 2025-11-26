package com.picksy.categoryservice.controller;

import com.picksy.categoryservice.exception.FileUploadException;
import com.picksy.categoryservice.request.OptionBody;
import com.picksy.categoryservice.response.OptionDTO;
import com.picksy.categoryservice.service.OptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.util.List;

@RestController
@RequestMapping("/api/option")
@RequiredArgsConstructor
public class OptionController {

    private final OptionService optionService;

    @Operation(
            summary = "Get all options for a category",
            description = "Fetches all options associated with a specific category."
    )
    @GetMapping("/public/{catId}")
    public ResponseEntity<List<OptionDTO>> findAllForCategory(
            @Parameter(description = "ID of the category") @PathVariable Long catId
    ) throws BadRequestException {
        return ResponseEntity.ok(optionService.findAllForCategory(catId));
    }

    @Operation(
            summary = "Add a new option",
            description = "Creates a new option and assigns it to a category."
    )
    @PostMapping("/secure")
    public ResponseEntity<OptionDTO> addOption(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role,
            @Parameter(description = "Data for the new option") @RequestBody OptionBody optionBody
    ) throws BadRequestException {
        return ResponseEntity.status(HttpStatus.CREATED).body(optionService.add(userId, role, optionBody));
    }

    @Operation(
            summary = "Upload an image for an option",
            description = "Adds an image file to an existing option by option ID."
    )
    @PatchMapping(value = "/secure/image/{optId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> addOptionImage(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role,
            @Parameter(description = "Image file to upload") @RequestParam MultipartFile image,
            @Parameter(description = "ID of the option to attach the image to") @PathVariable Long optId
    ) throws BadRequestException, FileUploadException {
        optionService.addImage(userId, role, optId, image);
        return ResponseEntity.status(HttpStatus.CREATED).body("Option image added.");
    }

    @Operation(
            summary = "Delete an option",
            description = "Removes an option and its associated image by option ID."
    )
    @DeleteMapping("/secure/{optionId}")
    public ResponseEntity<String> deleteOption(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role,
            @Parameter(description = "ID of the option to delete") @PathVariable Long optionId
    ) throws BadRequestException, FileUploadException, MalformedURLException {
        optionService.remove(userId, role, optionId);
        return ResponseEntity.ok("Option removed.");
    }

    @Operation(
            summary = "Delete an option image",
            description = "Removes the image associated with an option by option ID."
    )
    @DeleteMapping("/secure/image/{optionId}")
    public ResponseEntity<String> deleteOptionImage(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role,
            @Parameter(description = "ID of the option") @PathVariable Long optionId
    ) throws BadRequestException, FileUploadException, MalformedURLException {
        optionService.removeImg(userId, role, optionId);
        return ResponseEntity.ok("Option image removed.");
    }

    @Operation(
            summary = "Update option information",
            description = "Updates the name of an existing option by option ID."
    )
    @PatchMapping("/secure/{optId}")
    public ResponseEntity<String> updateOption(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role,
            @Parameter(description = "ID of the option") @PathVariable Long optId,
            @Parameter(description = "New name for the option") @RequestBody String name
    ) throws BadRequestException {
        optionService.update(userId, role, optId, name);
        return ResponseEntity.ok("Option updated.");
    }
}
