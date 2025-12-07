package com.picksy.categoryservice.controller;

import com.picksy.categoryservice.request.CreateCategorySetBody;
import com.picksy.categoryservice.request.UpdateCategorySetBody;
import com.picksy.categoryservice.response.CategorySetDTO;
import com.picksy.categoryservice.service.CategorySetService;
import lombok.RequiredArgsConstructor;
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

    @PostMapping("/secure")
    public ResponseEntity<CategorySetDTO> create(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody CreateCategorySetBody body) {
        CategorySetDTO dto = categorySetService.create(userId, body);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @PatchMapping("/secure/add")
    public ResponseEntity<CategorySetDTO> addCategory(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role,
            @RequestBody UpdateCategorySetBody body) {
        CategorySetDTO dto = categorySetService.addCategory(userId, role, body);
        return ResponseEntity.ok(dto);
    }

    @PatchMapping("/secure/remove")
    public ResponseEntity<CategorySetDTO> removeCategory(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role,
            @RequestBody UpdateCategorySetBody body) {
        CategorySetDTO dto = categorySetService.removeCategory(userId, role, body);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/secure/{setId}")
    public ResponseEntity<Void> deleteSet(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role,
            @PathVariable Long setId) {
        categorySetService.deleteSet(userId, role, setId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/public/{setId}/views")
    public ResponseEntity<CategorySetDTO> increaseViews(@PathVariable Long setId){
        categorySetService.increaseViews(setId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/public/{setId}")
    public ResponseEntity<CategorySetDTO> findSetWithCategories(@PathVariable Long setId) {
        CategorySetDTO dto = categorySetService.findSetWithCategories(setId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/public")
    public ResponseEntity<Page<CategorySetDTO>> findAllPublicSets(Pageable pageable) {
        Page<CategorySetDTO> page = categorySetService.findAllPublicSets(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/secure/author/{authorID}")
    public ResponseEntity<Page<CategorySetDTO>> findAllByAuthorID(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role,
            @PathVariable Long authorID,
            Pageable pageable) {
        Page<CategorySetDTO> page = categorySetService.findAllByAuthorID(userId, role, authorID, false, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/public/author/{authorID}")
    public ResponseEntity<Page<CategorySetDTO>> findAllPublicByAuthorID(
            @PathVariable Long authorID,
            Pageable pageable) {
        Page<CategorySetDTO> page = categorySetService.findAllByAuthorID((long)-1, "USER", authorID, true, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/secure/search/author/{authorID}")
    public ResponseEntity<Page<CategorySetDTO>> findAllByAuthorIDAndNameContaining(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role,
            @PathVariable Long authorID,
            @RequestParam String pattern,
            Pageable pageable) {
        Page<CategorySetDTO> page = categorySetService.findAllByAuthorIDAndNameContaining(userId, role, authorID, pattern, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/public/search")
    public ResponseEntity<Page<CategorySetDTO>> findAllPublicAndNameContaining(
            @RequestParam String pattern,
            Pageable pageable) {
        Page<CategorySetDTO> page = categorySetService.findAllPublicAndNameContaining(pattern, pageable);
        return ResponseEntity.ok(page);
    }
}