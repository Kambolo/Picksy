package com.picksy.categoryservice.repository;

import com.picksy.categoryservice.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Page<Category> findAllByAuthorID(Long id, Pageable pageable);
    Page<Category> findAllByAuthorIDAndIsPublic(Long id, Boolean isPublic, Pageable pageable);
    Page<Category> findByAuthorIDIsNull(Pageable pageable);
    Page<Category> findAllByIsPublic(boolean isPublic, Pageable pageable);
    Page<Category> findAllByIsPublicAndNameContainingIgnoreCase(Boolean isPublic, String name, Pageable pageable);
    Page<Category> findAllByAuthorIDAndNameContainingIgnoreCase(Long authorID, String name, Pageable pageable);
    Page<Category> findAllByAuthorIDAndIsPublicAndNameContainingIgnoreCase(Long authorID, Boolean isPublic, String name, Pageable pageable);
}


