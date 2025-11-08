package com.picksy.categoryservice.controller;

import com.picksy.categoryservice.exception.FileUploadException;
import com.picksy.categoryservice.request.CategoryBody;
import com.picksy.categoryservice.response.CategoryDTO;
import com.picksy.categoryservice.response.CategoryWithOptionsDTO;
import com.picksy.categoryservice.service.CategoryService;
import com.picksy.categoryservice.service.OptionService;
import com.picksy.categoryservice.util.enums.Type;
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
    private final OptionService optionService;

    @Operation(
            summary = "Create a new category",
            description = "Creates a new category with the provided data."
    )
    @PostMapping("/secure")
    public ResponseEntity<CategoryDTO> createCategory(
            @Parameter(description = "Category data to create") @RequestBody CategoryBody catBody
    ) throws BadRequestException {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(catBody));
    }

    @Operation(
            summary = "Upload an image for a category",
            description = "Adds an image to an existing category by category ID."
    )
    @PatchMapping(value = "/secure/image/{catId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> addCategoryImage(
            @Parameter(description = "Image file to upload") @RequestParam MultipartFile image,
            @Parameter(description = "ID of the category to attach the image to") @PathVariable Long catId
    ) throws BadRequestException, FileUploadException {
        categoryService.addImage(catId, image);
        return ResponseEntity.status(HttpStatus.CREATED).body("Category image added.");
    }

    @Operation(
            summary = "Get all public categories",
            description = "Fetches all public categories with optional pagination and sorting."
    )
    @GetMapping("/public")
    public ResponseEntity<Page<CategoryDTO>> getAllPublicCategories(
            @Parameter(description = "Page number, default is 0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size, default is 5") @RequestParam(defaultValue = "5") int size,
            @Parameter(description = "Field to sort by, default is 'id'") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort ascending, default is true") @RequestParam(defaultValue = "true") boolean ascending
    ) {
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(categoryService.findAllPublic(pageable));
    }

    @Operation(
            summary = "Get all categories for an author",
            description = "Fetches categories created by a specific author with optional pagination and sorting."
    )
    @GetMapping("/secure/author/{authorId}")
    public Page<CategoryDTO> getAllCategoriesForAuthor(
            @RequestHeader("X-User-Id") Long userId,
            @Parameter(description = "Author's user ID") @PathVariable Long authorId,
            @Parameter(description = "Page number, default is 0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size, default is 5") @RequestParam(defaultValue = "5") int size,
            @Parameter(description = "Field to sort by, default is 'id'") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort ascending, default is true") @RequestParam(defaultValue = "true") boolean ascending
    ) throws BadRequestException {
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return categoryService.findAllByAuthorID(userId, pageable, authorId, false);
    }

    @Operation(
            summary = "Get all public categories for an author",
            description = "Fetches public categories created by a specific author with optional pagination and sorting."
    )
    @GetMapping("/public/author/{authorId}")
    public Page<CategoryDTO> getAllPublicCategoriesForAuthor(
            @Parameter(description = "Author's user ID") @PathVariable Long authorId,
            @Parameter(description = "Page number, default is 0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size, default is 5") @RequestParam(defaultValue = "5") int size,
            @Parameter(description = "Field to sort by, default is 'id'") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort ascending, default is true") @RequestParam(defaultValue = "true") boolean ascending
    ) throws BadRequestException {
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return categoryService.findAllByAuthorID(authorId, pageable, authorId, true);
    }

    @Operation(
            summary = "Get built-in categories",
            description = "Fetches all built-in categories with optional pagination and sorting."
    )
    @GetMapping("/public/builtIn")
    public Page<CategoryDTO> getBuiltInCategories(
            @Parameter(description = "Page number, default is 0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size, default is 5") @RequestParam(defaultValue = "5") int size,
            @Parameter(description = "Field to sort by, default is 'id'") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort ascending, default is true") @RequestParam(defaultValue = "true") boolean ascending
    ) {
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return categoryService.findBuiltInCategories(pageable);
    }

    @Operation(
            summary = "Search public categories by pattern",
            description = "Fetches public categories matching a given pattern with optional pagination and sorting."
    )
    @GetMapping("/public/search")
    public Page<CategoryDTO> getPublicCategoriesByPattern(
            @Parameter(description = "Search pattern") @RequestParam String pattern,
            @Parameter(description = "Page number, default is 0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size, default is 5") @RequestParam(defaultValue = "5") int size,
            @Parameter(description = "Field to sort by, default is 'id'") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort ascending, default is true") @RequestParam(defaultValue = "true") boolean ascending
    ) {
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return categoryService.findPublicCategoriesByPattern(pattern, pageable);
    }

    @Operation(
            summary = "Search user categories by pattern",
            description = "Fetches categories created by a user that match a given pattern, with optional pagination and sorting."
    )
    @GetMapping("/secure/search/author/{authorId}")
    public Page<CategoryDTO> getUserCategoriesByPattern(
            @RequestHeader("X-User-Id") Long userId,
            @Parameter(description = "User ID") @PathVariable Long authorId,
            @Parameter(description = "Search pattern") @RequestParam String pattern,
            @Parameter(description = "Page number, default is 0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size, default is 5") @RequestParam(defaultValue = "5") int size,
            @Parameter(description = "Field to sort by, default is 'id'") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort ascending, default is true") @RequestParam(defaultValue = "true") boolean ascending
    ) throws BadRequestException {
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return categoryService.findUserCategoriesByPattern(userId, authorId, pattern, pageable, false);
    }

    @Operation(
            summary = "Search public user categories by pattern",
            description = "Fetches public categories created by a user that match a given pattern, with optional pagination and sorting."
    )
    @GetMapping("/public/search/author/{authorId}")
    public Page<CategoryDTO> getPublicUserCategoriesByPattern(
            @Parameter(description = "User ID") @PathVariable Long authorId,
            @Parameter(description = "Search pattern") @RequestParam String pattern,
            @Parameter(description = "Page number, default is 0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size, default is 5") @RequestParam(defaultValue = "5") int size,
            @Parameter(description = "Field to sort by, default is 'id'") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort ascending, default is true") @RequestParam(defaultValue = "true") boolean ascending
    ) throws BadRequestException {
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return categoryService.findUserCategoriesByPattern(authorId, authorId, pattern, pageable, true);
    }

    @Operation(
            summary = "Delete a category",
            description = "Removes a category and its associated image by ID."
    )
    @DeleteMapping("/secure/{catId}")
    public ResponseEntity<String> deleteCategory(
            @Parameter(description = "ID of the category to delete") @PathVariable Long catId
    ) throws BadRequestException, FileUploadException, MalformedURLException {
        categoryService.remove(catId);
        return ResponseEntity.ok("Category removed.");
    }

    @Operation(
            summary = "Delete a category image",
            description = "Removes the image associated with a category by ID."
    )
    @DeleteMapping("/secure/image/{catId}")
    public ResponseEntity<String> deleteCategoryImage(
            @Parameter(description = "ID of the category") @PathVariable Long catId
    ) throws BadRequestException, FileUploadException, MalformedURLException {
        categoryService.removeImg(catId);
        return ResponseEntity.ok("Category image removed.");
    }

    @Operation(
            summary = "Update category information",
            description = "Updates the details of an existing category by ID."
    )
    @PatchMapping("/secure/{catId}")
    public ResponseEntity<String> updateCategory(
            @Parameter(description = "ID of the category") @PathVariable Long catId,
            @Parameter(description = "Updated category data") @RequestBody CategoryBody categoryBody
    ) throws BadRequestException {
        categoryService.updateCategory(catId, categoryBody);
        return ResponseEntity.ok("Category updated.");
    }

    @Operation(
            summary = "Increase category view count",
            description = "Increments the number of views for a category by ID."
    )
    @PatchMapping("/public/{id}/increase")
    public ResponseEntity<String> increaseCategory(
            @Parameter(description = "ID of the category") @PathVariable Long id
    ) throws BadRequestException {
        categoryService.increaseViews(id);
        return ResponseEntity.ok("Category view count increased.");
    }

    @Operation(
            summary = "Get category details",
            description = "Fetches the detailed information of a category by ID."
    )
    @GetMapping("/public/{id}/details")
    public ResponseEntity<CategoryDTO> getCategory(
            @Parameter(description = "ID of the category") @PathVariable Long id
    ) throws BadRequestException {
        return ResponseEntity.ok(categoryService.findDTOById(id));
    }

    @Operation(
            summary = "Get category type",
            description = "Fetches the type of a category by ID."
    )
    @GetMapping("/public/{id}/type")
    public ResponseEntity<Type> getType(
            @Parameter(description = "ID of the category") @PathVariable Long id
    ) throws BadRequestException {
        return ResponseEntity.ok(categoryService.findTypeById(id));
    }

    @GetMapping("/public/{id}/options")
    public ResponseEntity<CategoryWithOptionsDTO> getCategoryWithOptions(@PathVariable Long id) throws BadRequestException {
        return ResponseEntity.ok().body(optionService.findCategoryWithOptions(id));
    }
}
