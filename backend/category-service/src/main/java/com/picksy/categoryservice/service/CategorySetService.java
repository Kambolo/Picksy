package com.picksy.categoryservice.service;

import com.picksy.DeletionEvent;
import com.picksy.categoryservice.exception.ForbiddenAccessException;
import com.picksy.categoryservice.exception.InvalidRequestException;
import com.picksy.categoryservice.exception.ResourceNotFoundException;
import com.picksy.categoryservice.model.Category;
import com.picksy.categoryservice.model.CategorySet;
import com.picksy.categoryservice.repository.CategoryRepository;
import com.picksy.categoryservice.repository.CategorySetRepository;
import com.picksy.categoryservice.request.CreateCategorySetBody;
import com.picksy.categoryservice.request.UpdateCategorySetBody;
import com.picksy.categoryservice.response.CategoryDTO;
import com.picksy.categoryservice.response.CategorySetDTO;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategorySetService {
  private final CategorySetRepository categorySetRepository;
  private final CategoryService categoryService;
  private final CategoryRepository categoryRepository;

    private static final String TOPIC = "category-deletion-topic";
    private final KafkaTemplate<String, Object> kafkaTemplate;

  @Transactional
  public CategorySetDTO create(Long userId, CreateCategorySetBody createCategorySetBody) {
    if (createCategorySetBody.name() == null || createCategorySetBody.name().isBlank())
      throw new InvalidRequestException("Name is required.");

    CategorySet categorySet =
        CategorySet.builder()
            .name(createCategorySetBody.name())
            .authorID(userId)
            .isPublic(createCategorySetBody.isPublic())
            .views(0)
            .created(LocalDateTime.now().plusHours(1))
            .build();

    categorySetRepository.save(categorySet);
    return mapToDTO(categorySet);
  }


  @Transactional
  public CategorySetDTO removeCategory(
      Long userId, String role, UpdateCategorySetBody updateCategorySetBody) {
      CategorySet categorySet = findById(updateCategorySetBody.setId());

      checkAuthor(userId, role, categorySet);

      Category category = categoryService.findById(updateCategorySetBody.categoryId());
      categorySet.remove(category);
      categoryRepository.delete(category);

      categorySetRepository.save(categorySet);
    return mapToDTO(categorySet);
  }

  @Transactional
  public void deleteSet(Long userId, String role, Long setId) {
    CategorySet categorySet = findById(setId);

    checkAuthor(userId, role, categorySet);
    categorySetRepository.delete(categorySet);
      kafkaTemplate.send(TOPIC, new DeletionEvent(setId, "SET"));
  }

  public CategorySetDTO findSetWithCategories(Long setId) {
    CategorySet categorySet =
        categorySetRepository
            .findByIdWithCategories(setId)
            .orElseThrow(
                () -> new ResourceNotFoundException("Category Set not found with ID: " + setId));
    return mapToDTO(categorySet);
  }

  @Transactional
  public CategorySetDTO updateCategorySet(
      Long userId, String role, Long setId, CreateCategorySetBody updateCategorySetBody) {
    CategorySet categorySet = findById(setId);
    checkAuthor(userId, role, categorySet);

    if (updateCategorySetBody.name() != null && !updateCategorySetBody.name().isBlank()) {
      categorySet.setName(updateCategorySetBody.name());
    }

    if (updateCategorySetBody.isPublic() != null) {
      categorySet.setIsPublic(updateCategorySetBody.isPublic());
    }

    categorySetRepository.save(categorySet);
    return mapToDTO(categorySet);
  }

  public Page<CategorySetDTO> findAllPublicSets(Pageable pageable) {
    return categorySetRepository.findAllPublicWithCategories(pageable).map(this::mapToDTO);
  }

  public Page<CategorySetDTO> findAllByAuthorID(
      Long userId, String role, Long authorID, Boolean isPublic, Pageable pageable) {

    Page<CategorySet> sets;

    if (Boolean.TRUE.equals(isPublic)) {
      sets = categorySetRepository.findAllPublicByAuthorIDWithCategories(authorID, pageable);
    } else {
      checkDataAccess(userId, role, authorID);
      sets = categorySetRepository.findAllByAuthorIDWithCategories(authorID, pageable);
    }

    return sets.map(this::mapToDTO);
  }

  public Page<CategorySetDTO> findAllByAuthorIDAndNameContaining(
      Long userId, String role, Long authorID, String pattern, Pageable pageable) {

    checkDataAccess(userId, role, authorID);

    Page<CategorySet> sets =
        categorySetRepository.findAllByAuthorIDAndNameContainingWithCategories(
            authorID, pattern, pageable);

    return sets.map(this::mapToDTO);
  }

  public Page<CategorySetDTO> findAllPublicAndNameContaining(String pattern, Pageable pageable) {

    Page<CategorySet> sets =
        categorySetRepository.findAllPublicAndNameContainingWithCategories(pattern, pageable);

    return sets.map(this::mapToDTO);
  }

  public void increaseViews(Long setId) {
    CategorySet categorySet = findById(setId);
    categorySet.setViews(categorySet.getViews() + 1);
    categorySetRepository.save(categorySet);
  }


  private void checkAuthor(Long userId, String role, CategorySet categorySet) {
    if (role.equals("ADMIN")) return;
    if (!userId.equals(categorySet.getAuthorID())) {
      throw new ForbiddenAccessException("You are not allowed to modify this category set.");
    }
  }

  private CategorySet findById(Long setId) {
    return categorySetRepository
        .findById(setId)
        .orElseThrow(
            () -> new ResourceNotFoundException("Category Set not found with ID: " + setId));
  }

  private CategorySetDTO mapToDTO(CategorySet categorySet) {
    List<CategoryDTO> categories = new ArrayList<>();
    if (categorySet.getCategories() != null)
      categories = categorySet.getCategories().stream().map(categoryService::mapToDTO).toList();
    return new CategorySetDTO(
        categorySet.getId(),
        categorySet.getName(),
        categories,
        categorySet.getAuthorID(),
        categorySet.getViews(),
        categorySet.getCreated(),
        categorySet.getIsPublic());
  }

  private void checkDataAccess(Long userId, String role, Long authorID) {
    if (!role.equals("ADMIN") && !userId.equals(authorID)) {
      throw new ForbiddenAccessException("Access denied. Author ID does not match user ID.");
    }
  }
}
