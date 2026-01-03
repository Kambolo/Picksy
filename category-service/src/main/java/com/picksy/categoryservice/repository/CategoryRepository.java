package com.picksy.categoryservice.repository;

import com.picksy.categoryservice.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Page<Category> findAllByCategorySetIsNullAndAuthorID(Long id, Pageable pageable);
    Page<Category> findAllByCategorySetIsNullAndAuthorIDAndIsPublic(Long id, Boolean isPublic, Pageable pageable);
    Page<Category> findAllByCategorySetIsNullAndIsPublic(Boolean isPublic, Pageable pageable);
    Page<Category> findAllByCategorySetIsNullAndIsPublicAndNameContainingIgnoreCase(Boolean isPublic, String name, Pageable pageable);
    Page<Category> findAllByCategorySetIsNullAndAuthorIDAndNameContainingIgnoreCase(Long authorID, String name, Pageable pageable);
    Page<Category> findAllByCategorySetIsNullAndAuthorIDAndIsPublicAndNameContainingIgnoreCase(Long authorID, Boolean isPublic, String name, Pageable pageable);
}