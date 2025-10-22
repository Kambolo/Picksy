package com.picksy.categoryservice.controller;

import com.picksy.categoryservice.exception.FileUploadException;
import com.picksy.categoryservice.request.CategoryBody;
import com.picksy.categoryservice.response.CategoryDTO;
import com.picksy.categoryservice.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;


@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Create a new category", description = "Creates a new category using the provided request body.")
    @PostMapping("/secure")
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody CategoryBody catBody) throws BadRequestException {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(catBody));
    }

    @Operation(summary = "Upload an image for a category", description = "Adds an image to an existing category by category ID.")
    @PostMapping(value = "/secure/image/{catId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> addCategoryImage(
            @Parameter(description = "Image file to upload") @RequestParam MultipartFile image,
            @Parameter(description = "ID of the category to attach the image to") @PathVariable Long catId
    ) throws BadRequestException, FileUploadException {
        categoryService.addImage(catId, image);
        return ResponseEntity.status(HttpStatus.CREATED).body("Category image added.");
    }

    @Operation(summary = "Get all categories with pagination", description = "Fetches all categories with optional pagination, sorting, and ordering.")
    @GetMapping("/public")
    public ResponseEntity<Page<CategoryDTO>> getAllCategories(
            @Parameter(description = "Page number (default is 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size (default is 5)") @RequestParam(defaultValue = "5") int size,
            @Parameter(description = "Sort by field (default is 'id')") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort ascending? (default is true)") @RequestParam(defaultValue = "true") boolean ascending
    ) {
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.findAll(pageable));
    }

    @Operation(summary = "Get a categories for author with pagination", description = "Fetches all categories for author with optional pagination, sorting, and ordering.")
    @GetMapping("/public/{authorId}")
    public Page<CategoryDTO> getAllCategoriesForAuthor(
            @Parameter(description = "User ID") @PathVariable("authorId") Long authorId,
            @Parameter(description = "Page number (default is 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size (default is 5)") @RequestParam(defaultValue = "5") int size,
            @Parameter(description = "Sort by field (default is 'id')") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort ascending? (default is true)") @RequestParam(defaultValue = "true") boolean ascending
    ) throws BadRequestException {
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return categoryService.findAllByAuthorID(pageable, authorId);
    }

    @Operation(summary = "Get built in categories with pagination", description = "Fetches all built in categories with optional pagination, sorting, and ordering.")
    @GetMapping("/public/builtIn")
    public Page<CategoryDTO> getBuiltInCategories(
            @Parameter(description = "Page number (default is 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size (default is 5)") @RequestParam(defaultValue = "5") int size,
            @Parameter(description = "Sort ascending? (default is true)") @RequestParam(defaultValue = "true") boolean ascending
    ){
        Pageable pageable = PageRequest.of(page, size);
        return categoryService.findBuiltInCategories(pageable);
    }

    @Operation(summary = "Get categories containing given pattern with pagination", description = "Fetches all categories based on given pattern with optional pagination, sorting, and ordering.")
    @GetMapping("/public/search")
    public Page<CategoryDTO> getCategoriesByPattern(
            @Parameter(description = "Pattern") @RequestParam String pattern,
            @Parameter(description = "Page number (default is 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size (default is 5)") @RequestParam(defaultValue = "5") int size,
            @Parameter(description = "Sort by field (default is 'id')") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort ascending? (default is true)") @RequestParam(defaultValue = "true") boolean ascending
    ){
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return categoryService.findCategoriesByPattern(pattern, pageable);
    }

    @Operation(summary = "Delete a category", description = "Removes a category and its associated image by category ID.")
    @DeleteMapping("/secure/{catId}")
    public ResponseEntity<String> deleteCategory(
            @Parameter(description = "ID of the category to delete") @PathVariable Long catId
    ) throws BadRequestException, FileUploadException, MalformedURLException {
        categoryService.remove(catId);
        return ResponseEntity.status(HttpStatus.OK).body("Category removed.");
    }

    @Operation(summary = "Delete a category image", description = "Removes a category image by category ID.")
    @DeleteMapping("/secure/image/{catId}")
    public ResponseEntity<String> deleteCategoryImage(
            @Parameter(description = "ID of the category ") @PathVariable Long catId
    ) throws BadRequestException, FileUploadException, MalformedURLException {
        categoryService.removeImg(catId);
        return ResponseEntity.status(HttpStatus.OK).body("Category image removed.");
    }

    @PatchMapping("/secure/{catId}")
    @Operation(
            summary = "Update category info",
            description = "Updates the category associated with the given category ID."
    )
    public ResponseEntity<String> updateCategory(@PathVariable Long catId, CategoryBody categoryBody) throws BadRequestException {
        categoryService.update(catId, categoryBody);
        return ResponseEntity.ok().body("Category updated.");
    }

    @PatchMapping("/public/{id}/increase")
    public ResponseEntity<String> increaseCategory(@PathVariable Long id) throws BadRequestException {
        categoryService.increaseViews(id);
        return ResponseEntity.status(HttpStatus.OK).body("Category updated.");
    }
}
