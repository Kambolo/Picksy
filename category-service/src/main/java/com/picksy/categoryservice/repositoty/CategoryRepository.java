package com.picksy.categoryservice.repositoty;

import com.picksy.categoryservice.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findById(Long id);
    Page<Category> findAllByAuthorIgnoreCase(String author, Pageable pageable);
}
