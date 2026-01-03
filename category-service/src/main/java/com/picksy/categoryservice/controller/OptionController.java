package com.picksy.categoryservice.controller;

import com.picksy.categoryservice.exception.FileUploadException;
import com.picksy.categoryservice.request.OptionBody;
import com.picksy.categoryservice.response.OptionDTO;
import com.picksy.categoryservice.service.OptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
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
            summary = "Get category options",
            description = "Returns all options assigned to a specific category.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Options returned",
                            content = @Content(schema = @Schema(implementation = OptionDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Category not found")
            }
    )
    @GetMapping("/public/{catId}")
    public ResponseEntity<List<OptionDTO>> findAllForCategory(
            @Parameter(description = "Category ID", required = true)
            @PathVariable Long catId) {
        return ResponseEntity.ok(optionService.findAllForCategory(catId));
    }

    @Operation(
            summary = "Create option",
            description = "Creates a new option and assigns it to a category.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Option created",
                            content = @Content(schema = @Schema(implementation = OptionDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Access denied"),
                    @ApiResponse(responseCode = "400", description = "Invalid request")
            }
    )
    @PostMapping("/secure")
    public ResponseEntity<OptionDTO> addOption(
            @Parameter(description = "Authenticated user ID", required = true)
            @RequestHeader("X-User-Id") Long userId,
            @Parameter(description = "Authenticated user role", required = true)
            @RequestHeader("X-User-Role") String role,
            @Parameter(description = "Option creation data", required = true)
            @RequestBody OptionBody optionBody) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(optionService.add(userId, role, optionBody));
    }

    @Operation(
            summary = "Upload option image",
            description = "Uploads and assigns an image to an existing option.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Image uploaded"),
                    @ApiResponse(responseCode = "403", description = "Access denied"),
                    @ApiResponse(responseCode = "400", description = "Invalid file")
            }
    )
    @PatchMapping(value = "/secure/image/{optId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> addOptionImage(
            @Parameter(description = "Authenticated user ID", required = true)
            @RequestHeader("X-User-Id") Long userId,
            @Parameter(description = "Authenticated user role", required = true)
            @RequestHeader("X-User-Role") String role,
            @Parameter(description = "Image file", required = true)
            @RequestParam MultipartFile image,
            @Parameter(description = "Option ID", required = true)
            @PathVariable Long optId) throws FileUploadException {
        optionService.addImage(userId, role, optId, image);
        return ResponseEntity.status(HttpStatus.CREATED).body("Option image added.");
    }

    @Operation(
            summary = "Delete option",
            description = "Deletes an option together with its image.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Option deleted"),
                    @ApiResponse(responseCode = "403", description = "Access denied"),
                    @ApiResponse(responseCode = "404", description = "Option not found")
            }
    )
    @DeleteMapping("/secure/{optionId}")
    public ResponseEntity<String> deleteOption(
            @Parameter(description = "Authenticated user ID", required = true)
            @RequestHeader("X-User-Id") Long userId,
            @Parameter(description = "Authenticated user role", required = true)
            @RequestHeader("X-User-Role") String role,
            @Parameter(description = "Option ID", required = true)
            @PathVariable Long optionId)
            throws FileUploadException, MalformedURLException {
        optionService.remove(userId, role, optionId);
        return ResponseEntity.ok("Option removed.");
    }

    @Operation(
            summary = "Delete option image",
            description = "Deletes the image assigned to an option.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Image removed"),
                    @ApiResponse(responseCode = "403", description = "Access denied"),
                    @ApiResponse(responseCode = "404", description = "Option not found")
            }
    )
    @DeleteMapping("/secure/image/{optionId}")
    public ResponseEntity<String> deleteOptionImage(
            @Parameter(description = "Authenticated user ID", required = true)
            @RequestHeader("X-User-Id") Long userId,
            @Parameter(description = "Authenticated user role", required = true)
            @RequestHeader("X-User-Role") String role,
            @Parameter(description = "Option ID", required = true)
            @PathVariable Long optionId)
            throws FileUploadException, MalformedURLException {
        optionService.removeImg(userId, role, optionId);
        return ResponseEntity.ok("Option image removed.");
    }

    @Operation(
            summary = "Update option",
            description = "Updates the name of an existing option.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Option updated"),
                    @ApiResponse(responseCode = "403", description = "Access denied"),
                    @ApiResponse(responseCode = "404", description = "Option not found")
            }
    )
    @PatchMapping("/secure/{optId}")
    public ResponseEntity<String> updateOption(
            @Parameter(description = "Authenticated user ID", required = true)
            @RequestHeader("X-User-Id") Long userId,
            @Parameter(description = "Authenticated user role", required = true)
            @RequestHeader("X-User-Role") String role,
            @Parameter(description = "Option ID", required = true)
            @PathVariable Long optId,
            @Parameter(description = "New option name", required = true)
            @RequestBody String name) {
        optionService.update(userId, role, optId, name);
        return ResponseEntity.ok("Option updated.");
    }
}
