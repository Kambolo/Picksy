package com.picksy.categoryservice.repository;

import com.picksy.categoryservice.model.CategorySet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategorySetRepository extends JpaRepository<CategorySet, Long> {
  @Query("SELECT cs FROM CategorySet cs LEFT JOIN FETCH cs.categories WHERE cs.id = :id")
  Optional<CategorySet> findByIdWithCategories(@Param("id") Long id);

  @Query(
      value = "SELECT cs FROM CategorySet cs JOIN FETCH cs.categories WHERE cs.isPublic = TRUE",
      countQuery = "SELECT count(cs) FROM CategorySet cs WHERE cs.isPublic = TRUE")
  Page<CategorySet> findAllPublicWithCategories(Pageable pageable);

  @Query(
      value =
          "SELECT cs FROM CategorySet cs JOIN FETCH cs.categories WHERE cs.authorID = :authorID",
      countQuery = "SELECT count(cs) FROM CategorySet cs WHERE cs.authorID = :authorID")
  Page<CategorySet> findAllByAuthorIDWithCategories(
      @Param("authorID") Long authorID, Pageable pageable);

  @Query(
      value =
          "SELECT cs FROM CategorySet cs JOIN FETCH cs.categories WHERE cs.authorID = :authorID AND cs.isPublic = TRUE",
      countQuery =
          "SELECT count(cs) FROM CategorySet cs WHERE cs.authorID = :authorID AND cs.isPublic = TRUE")
  Page<CategorySet> findAllPublicByAuthorIDWithCategories(
      @Param("authorID") Long authorID, Pageable pageable);

  @Query(
      value =
          "SELECT cs FROM CategorySet cs JOIN FETCH cs.categories WHERE cs.authorID = :authorID AND LOWER(cs.name) LIKE LOWER(CONCAT('%', :pattern, '%'))",
      countQuery =
          "SELECT count(cs) FROM CategorySet cs WHERE cs.authorID = :authorID AND LOWER(cs.name) LIKE LOWER(CONCAT('%', :pattern, '%'))")
  Page<CategorySet> findAllByAuthorIDAndNameContainingWithCategories(
      @Param("authorID") Long authorID, @Param("pattern") String pattern, Pageable pageable);

  @Query(
      value =
          "SELECT cs FROM CategorySet cs JOIN FETCH cs.categories WHERE cs.isPublic = TRUE AND LOWER(cs.name) LIKE LOWER(CONCAT('%', :pattern, '%'))",
      countQuery =
          "SELECT count(cs) FROM CategorySet cs WHERE cs.isPublic = TRUE AND LOWER(cs.name) LIKE LOWER(CONCAT('%', :pattern, '%'))")
  Page<CategorySet> findAllPublicAndNameContainingWithCategories(
      @Param("pattern") String pattern, Pageable pageable);
}
