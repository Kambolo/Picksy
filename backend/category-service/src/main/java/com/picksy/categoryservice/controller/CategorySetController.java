package com.picksy.categoryservice.controller;

import com.picksy.categoryservice.request.CreateCategorySetBody;
import com.picksy.categoryservice.request.UpdateCategorySetBody;
import com.picksy.categoryservice.response.CategorySetDTO;
import com.picksy.categoryservice.service.CategorySetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import okhttp3.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/category-set")
@RequiredArgsConstructor
public class CategorySetController {

  private final CategorySetService categorySetService;

  @Operation(
      summary = "Create category set",
      description = "Creates a new category set assigned to the authenticated user.",
      responses = {
        @ApiResponse(
            responseCode = "201",
            description = "Category set created",
            content = @Content(schema = @Schema(implementation = CategorySetDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request")
      })
  @PostMapping("/secure")
  public ResponseEntity<CategorySetDTO> create(
      @Parameter(description = "Authenticated user ID", required = true) @RequestHeader("X-User-Id")
          Long userId,
      @Parameter(description = "Category set creation data", required = true) @RequestBody
          CreateCategorySetBody body) {
    CategorySetDTO dto = categorySetService.create(userId, body);
    return new ResponseEntity<>(dto, HttpStatus.CREATED);
  }

  @Operation(
      summary = "Remove category from set",
      description = "Removes a category from a category set.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Category removed from set",
            content = @Content(schema = @Schema(implementation = CategorySetDTO.class))),
        @ApiResponse(responseCode = "403", description = "Access denied")
      })
  @PatchMapping("/secure/remove")
  public ResponseEntity<CategorySetDTO> removeCategory(
      @Parameter(description = "Authenticated user ID", required = true) @RequestHeader("X-User-Id")
          Long userId,
      @Parameter(description = "Authenticated user role", required = true)
          @RequestHeader("X-User-Role")
          String role,
      @Parameter(description = "Category set update data", required = true) @RequestBody
          UpdateCategorySetBody body) {
    CategorySetDTO dto = categorySetService.removeCategory(userId, role, body);
    return ResponseEntity.ok(dto);
  }

  @Operation(
      summary = "Delete category set",
      description = "Deletes a category set with all its relations.",
      responses = {
        @ApiResponse(responseCode = "204", description = "Category set deleted"),
        @ApiResponse(responseCode = "403", description = "Access denied")
      })
  @DeleteMapping("/secure/{setId}")
  public ResponseEntity<Void> deleteSet(
      @Parameter(description = "Authenticated user ID", required = true) @RequestHeader("X-User-Id")
          Long userId,
      @Parameter(description = "Authenticated user role", required = true)
          @RequestHeader("X-User-Role")
          String role,
      @Parameter(description = "Category set ID", required = true) @PathVariable Long setId) {
    categorySetService.deleteSet(userId, role, setId);
    return ResponseEntity.noContent().build();
  }

  @Operation(
      summary = "Increase category set views",
      description = "Increments the view counter of a category set.",
      responses = {@ApiResponse(responseCode = "204", description = "View counter increased")})
  @PatchMapping("/public/{setId}/views")
  public ResponseEntity<CategorySetDTO> increaseViews(
      @Parameter(description = "Category set ID", required = true) @PathVariable Long setId) {
    categorySetService.increaseViews(setId);
    return ResponseEntity.noContent().build();
  }

  @Operation(
      summary = "Get category set details",
      description = "Returns a category set with its assigned categories.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Category set returned",
            content = @Content(schema = @Schema(implementation = CategorySetDTO.class))),
        @ApiResponse(responseCode = "404", description = "Category set not found")
      })
  @GetMapping("/public/{setId}")
  public ResponseEntity<CategorySetDTO> findSetWithCategories(
      @Parameter(description = "Category set ID", required = true) @PathVariable Long setId) {
    CategorySetDTO dto = categorySetService.findSetWithCategories(setId);
    return ResponseEntity.ok(dto);
  }

  @Operation(
      summary = "Get all public category sets",
      description = "Returns a paginated list of all public category sets.",
      responses = {@ApiResponse(responseCode = "200", description = "Category sets returned")})
  @GetMapping("/public")
  public ResponseEntity<Page<CategorySetDTO>> findAllPublicSets(Pageable pageable) {
    Page<CategorySetDTO> page = categorySetService.findAllPublicSets(pageable);
    return ResponseEntity.ok(page);
  }

  @Operation(
      summary = "Get author's category sets",
      description = "Returns all category sets created by the specified author.",
      responses = {
        @ApiResponse(responseCode = "200", description = "Category sets returned"),
        @ApiResponse(responseCode = "403", description = "Access denied")
      })
  @GetMapping("/secure/author/{authorID}")
  public ResponseEntity<Page<CategorySetDTO>> findAllByAuthorID(
      @Parameter(description = "Authenticated user ID", required = true) @RequestHeader("X-User-Id")
          Long userId,
      @Parameter(description = "Authenticated user role", required = true)
          @RequestHeader("X-User-Role")
          String role,
      @Parameter(description = "Author user ID", required = true) @PathVariable Long authorID,
      Pageable pageable) {
    Page<CategorySetDTO> page =
        categorySetService.findAllByAuthorID(userId, role, authorID, false, pageable);
    return ResponseEntity.ok(page);
  }

  @Operation(
      summary = "Get public author's category sets",
      description = "Returns public category sets created by the specified author.",
      responses = {@ApiResponse(responseCode = "200", description = "Category sets returned")})
  @GetMapping("/public/author/{authorID}")
  public ResponseEntity<Page<CategorySetDTO>> findAllPublicByAuthorID(
      @Parameter(description = "Author user ID", required = true) @PathVariable Long authorID,
      Pageable pageable) {
    Page<CategorySetDTO> page =
        categorySetService.findAllByAuthorID(-1L, "USER", authorID, true, pageable);
    return ResponseEntity.ok(page);
  }

  @Operation(
      summary = "Search author's category sets",
      description = "Searches category sets created by a specific author using a name pattern.",
      responses = {
        @ApiResponse(responseCode = "200", description = "Search results returned"),
        @ApiResponse(responseCode = "403", description = "Access denied")
      })
  @GetMapping("/secure/search/author/{authorID}")
  public ResponseEntity<Page<CategorySetDTO>> findAllByAuthorIDAndNameContaining(
      @Parameter(description = "Authenticated user ID", required = true) @RequestHeader("X-User-Id")
          Long userId,
      @Parameter(description = "Authenticated user role", required = true)
          @RequestHeader("X-User-Role")
          String role,
      @Parameter(description = "Author user ID", required = true) @PathVariable Long authorID,
      @Parameter(description = "Search pattern", required = true) @RequestParam String pattern,
      Pageable pageable) {
    Page<CategorySetDTO> page =
        categorySetService.findAllByAuthorIDAndNameContaining(
            userId, role, authorID, pattern, pageable);
    return ResponseEntity.ok(page);
  }

  @Operation(
      summary = "Search public category sets",
      description = "Searches public category sets using a name pattern.",
      responses = {@ApiResponse(responseCode = "200", description = "Search results returned")})
  @GetMapping("/public/search")
  public ResponseEntity<Page<CategorySetDTO>> findAllPublicAndNameContaining(
      @Parameter(description = "Search pattern", required = true) @RequestParam String pattern,
      Pageable pageable) {
    Page<CategorySetDTO> page =
        categorySetService.findAllPublicAndNameContaining(pattern, pageable);
    return ResponseEntity.ok(page);
  }

  @Operation(
      summary = "Update category set",
      description = "Updates category set.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Category set updated",
            content = @Content(schema = @Schema(implementation = CategorySetDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request")
      })
  @PatchMapping("/secure/{setId}")
  public ResponseEntity<CategorySetDTO> updateSet(
      @Parameter(description = "Category Set ID") @PathVariable String setId,
      @Parameter(description = "Authenticated user ID", required = true) @RequestHeader("X-User-Id")
          Long userId,
      @Parameter(description = "Authenticated user role", required = true)
      @RequestHeader("X-User-Role")
      String role,
      @Parameter(description = "Category set creation data", required = true) @RequestBody
          CreateCategorySetBody body) {
      return ResponseEntity.ok(categorySetService.updateCategorySet(userId, role, Long.parseLong(setId), body));
  }
}
