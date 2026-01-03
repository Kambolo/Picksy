package com.picksy.categoryservice.controller;

import com.picksy.categoryservice.exception.FileUploadException;
import com.picksy.categoryservice.request.CategoryBody;
import com.picksy.categoryservice.response.CategoryDTO;
import com.picksy.categoryservice.response.CategorySetDTO;
import com.picksy.categoryservice.response.CategoryWithOptionsDTO;
import com.picksy.categoryservice.service.CategoryService;
import com.picksy.categoryservice.service.OptionService;
import com.picksy.categoryservice.util.enums.Type;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
            summary = "Create category",
            description = "Creates a new category assigned to the authenticated user.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Category created",
                            content = @Content(schema = @Schema(implementation = CategoryDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request")
            }
    )
    @PostMapping("/secure")
    public ResponseEntity<CategoryDTO> createCategory(
            @Parameter(description = "Authenticated user ID", required = true)
            @RequestHeader("X-User-Id") Long userId,
            @Parameter(description = "Category data", required = true)
            @RequestBody CategoryBody catBody) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(userId, catBody));
    }

    @Operation(
            summary = "Upload category image",
            description = "Uploads or replaces an image assigned to a category.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Image uploaded"),
                    @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
                    @ApiResponse(responseCode = "404", description = "Category not found")
            }
    )
    @PostMapping(value = "/secure/{catId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> addCategoryImage(
            @Parameter(description = "Authenticated user ID", required = true)
            @RequestHeader("X-User-Id") Long userId,
            @Parameter(description = "Authenticated user role", required = true)
            @RequestHeader("X-User-Role") String role,
            @Parameter(description = "Image file", required = true)
            @RequestParam MultipartFile image,
            @Parameter(description = "Category ID", required = true)
            @PathVariable Long catId) throws FileUploadException {
        categoryService.addImage(userId, role, catId, image);
        return ResponseEntity.status(HttpStatus.CREATED).body("Category image added.");
    }

    @Operation(
            summary = "Get public categories",
            description = "Returns a paginated list of all public categories.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Categories returned")
            }
    )
    @GetMapping("/public")
    public ResponseEntity<Page<CategoryDTO>> getAllPublicCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending) {

        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(categoryService.findAllPublic(pageable));
    }

    @Operation(
            summary = "Get author's categories",
            description = "Returns all categories created by the specified author.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Categories returned"),
                    @ApiResponse(responseCode = "403", description = "Access denied")
            }
    )
    @GetMapping("/secure/author/{authorId}")
    public Page<CategoryDTO> getAllCategoriesForAuthor(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role,
            @PathVariable Long authorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending) {

        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return categoryService.findAllByAuthorID(userId, role, pageable, authorId, false);
    }

    @Operation(
            summary = "Get public author's categories",
            description = "Returns public categories created by the specified author.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Categories returned")
            }
    )
    @GetMapping("/public/author/{authorId}")
    public Page<CategoryDTO> getAllPublicCategoriesForAuthor(
            @PathVariable Long authorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending) {

        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return categoryService.findAllByAuthorID(authorId, "USER", pageable, authorId, true);
    }

    @Operation(
            summary = "Get built-in categories",
            description = "Returns categories predefined in the system.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Categories returned")
            }
    )
    @GetMapping("/public/builtIn")
    public Page<CategoryDTO> getBuiltInCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending) {

        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return categoryService.findBuiltInCategories(pageable);
    }

    @Operation(
            summary = "Search public categories",
            description = "Searches public categories by name or description pattern.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Search results returned")
            }
    )
    @GetMapping("/public/search")
    public ResponseEntity<Page<CategoryDTO>> getPublicCategoriesByPattern(
            @RequestParam String pattern,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending) {

        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(categoryService.findPublicCategoriesByPattern(pattern, pageable));
    }

    @Operation(
            summary = "Search user's categories",
            description = "Searches categories created by a specific user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Search results returned"),
                    @ApiResponse(responseCode = "403", description = "Access denied")
            }
    )
    @GetMapping("/secure/search/author/{authorId}")
    public Page<CategoryDTO> getUserCategoriesByPattern(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role,
            @PathVariable Long authorId,
            @RequestParam String pattern,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending) throws BadRequestException {

        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return categoryService.findUserCategoriesByPattern(userId, authorId, role, pattern, pageable, false);
    }

    @Operation(
            summary = "Delete category",
            description = "Deletes a category along with its image.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Category deleted"),
                    @ApiResponse(responseCode = "403", description = "Access denied"),
                    @ApiResponse(responseCode = "404", description = "Category not found")
            }
    )
    @DeleteMapping("/secure/{catId}")
    public ResponseEntity<String> deleteCategory(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role,
            @PathVariable Long catId) throws FileUploadException, MalformedURLException {

        categoryService.remove(userId, role, catId);
        return ResponseEntity.ok("Category removed.");
    }

    @Operation(
            summary = "Update category",
            description = "Updates category details.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Category updated"),
                    @ApiResponse(responseCode = "403", description = "Access denied")
            }
    )
    @PatchMapping("/secure/{catId}")
    public ResponseEntity<String> updateCategory(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role,
            @PathVariable Long catId,
            @RequestBody CategoryBody categoryBody) {

        categoryService.updateCategory(userId, role, catId, categoryBody);
        return ResponseEntity.ok("Category updated.");
    }

    @Operation(
            summary = "Get category details",
            description = "Returns detailed category data.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Category details returned")
            }
    )
    @GetMapping("/public/{id}/details")
    public ResponseEntity<CategoryDTO> getCategory(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.findDTOById(id));
    }

    @Operation(
            summary = "Get category options",
            description = "Returns category with all voting options.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Category with options returned")
            }
    )
    @GetMapping("/public/{id}/options")
    public ResponseEntity<CategoryWithOptionsDTO> getCategoryWithOptions(@PathVariable Long id) throws BadRequestException {
        return ResponseEntity.ok(optionService.findCategoryWithOptions(id));
    }

    @Operation(
            summary = "Get category type",
            description = "Returns the voting type of a category.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Category type returned")
            }
    )
    @GetMapping("/public/{id}/type")
    public ResponseEntity<Type> getType(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.findTypeById(id));
    }

    @Operation(
            summary = "Increase category views",
            description = "Increments the view counter of a category.",
            responses = {@ApiResponse(responseCode = "204", description = "View counter increased")})
    @PatchMapping("/public/{catId}/views")
    public ResponseEntity<CategoryDTO> increaseViews(
            @Parameter(description = "Category set ID", required = true) @PathVariable Long catId) {
        categoryService.increaseViews(catId);
        return ResponseEntity.noContent().build();
    }
}
