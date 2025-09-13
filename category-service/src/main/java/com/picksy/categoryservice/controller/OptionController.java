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

    @Operation(summary = "Finds all options for category", description = "Fetch all options for specific category.")
    @GetMapping("/public/{catId}")
    public ResponseEntity<List<OptionDTO>> findAllForCategory(@PathVariable Long catId) throws BadRequestException {
        return ResponseEntity.status(HttpStatus.OK).body(optionService.findAllForCategory(catId));
    }

    @Operation(summary = "Add a new option", description = "Creates a new option and assigns it to a category.")
    @PostMapping
    public ResponseEntity<String> addOption(@RequestBody OptionBody optionBody) throws BadRequestException {
        optionService.add(optionBody);
        return ResponseEntity.status(HttpStatus.CREATED).body("Option added.");
    }

    @Operation(summary = "Upload an image for an option", description = "Adds an image to an existing option by option ID.")
    @PostMapping(value = "/image/{optId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> addOptionImage(
            @Parameter(description = "Image file to upload") @RequestParam MultipartFile image,
            @Parameter(description = "ID of the option to attach the image to") @PathVariable Long optId
    ) throws BadRequestException, FileUploadException {
        optionService.addImage(optId, image);
        return ResponseEntity.status(HttpStatus.CREATED).body("Option image added.");
    }

    @Operation(summary = "Delete an option", description = "Removes an option and its associated image by option ID.")
    @DeleteMapping("{optionId}")
    public ResponseEntity<String> deleteOption(
            @Parameter(description = "ID of the option to delete") @PathVariable("optionId") Long optionId
    ) throws BadRequestException, FileUploadException, MalformedURLException {
        optionService.remove(optionId);
        return ResponseEntity.status(HttpStatus.OK).body("Option removed.");
    }

    @Operation(summary = "Delete an option image", description = "Removes an option image by option ID.")
    @DeleteMapping("/image/{optionId}")
    public ResponseEntity<String> deleteOptionImage(
            @Parameter(description = "ID of the option") @PathVariable("optionId") Long optionId
    ) throws BadRequestException, FileUploadException, MalformedURLException {
        optionService.removeImg(optionId);
        return ResponseEntity.status(HttpStatus.OK).body("Option image removed.");
    }

    @PatchMapping("{optId}")
    @Operation(
            summary = "Update option info",
            description = "Updates the option associated with the given option ID."
    )
    public ResponseEntity<String> updateOption(@PathVariable Long optId, String name) throws BadRequestException {
        optionService.update(optId, name);
        return ResponseEntity.ok().body("Option updated.");
    }
}
