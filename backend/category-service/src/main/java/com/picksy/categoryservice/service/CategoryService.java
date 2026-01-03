package com.picksy.categoryservice.service;

import com.picksy.categoryservice.exception.FileUploadException;
import com.picksy.categoryservice.exception.ForbiddenAccessException;
import com.picksy.categoryservice.exception.InvalidRequestException;
import com.picksy.categoryservice.exception.ResourceNotFoundException;
import com.picksy.categoryservice.model.Category;
import com.picksy.categoryservice.model.CategorySet;
import com.picksy.categoryservice.model.Option;
import com.picksy.categoryservice.repository.CategoryRepository;
import com.picksy.categoryservice.repository.CategorySetRepository;
import com.picksy.categoryservice.request.CategoryBody;
import com.picksy.categoryservice.response.CategoryDTO;
import com.picksy.categoryservice.util.enums.Type;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CategoryService {

  private final CategoryRepository categoryRepository;
  private final FileUploadService fileUploadService;
  private final CategorySetRepository categorySetRepository;

  private final String DEFAULT_PHOTO_URL =
      "https://res.cloudinary.com/dctiucda1/image/upload/v1764854844/image_sg0cb3.png";

  public CategoryDTO findDTOById(Long id) {
    Category category = findById(id);
    return mapToDTO(category);
  }

  protected Category findById(Long id) {
    return categoryRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));
  }

  public Page<CategoryDTO> findAllPublic(Pageable pageable) {
    return categoryRepository.findAllByCategorySetIsNullAndIsPublic(true, pageable).map(this::mapToDTO);
  }

  public Page<CategoryDTO> findAllByAuthorID(
      Long userId, String role, Pageable pageable, Long authorID, Boolean isPublic) {

    checkDataAccess(userId, role, authorID);

    Page<Category> categories;

    if (Boolean.TRUE.equals(isPublic)) {
      categories = categoryRepository.findAllByCategorySetIsNullAndAuthorIDAndIsPublic(authorID, true, pageable);
    } else {
      categories = categoryRepository.findAllByCategorySetIsNullAndAuthorID(authorID, pageable);
    }

    return categories.map(this::mapToDTO);
  }

  public Page<CategoryDTO> findBuiltInCategories(Pageable pageable) {
    return findAllByAuthorID((long)-1, "USER", pageable, (long)-1, true);
  }

  public Type findTypeById(Long id) {
    Category category = findById(id);
    return category.getType();
  }

  public Page<CategoryDTO> findPublicCategoriesByPattern(String pattern, Pageable pageable) {
    Page<Category> categories =
        categoryRepository.findAllByCategorySetIsNullAndIsPublicAndNameContainingIgnoreCase(
            true, pattern.toLowerCase(), pageable);

    return categories.map(this::mapToDTO);
  }

  public Page<CategoryDTO> findUserCategoriesByPattern(
      Long userId,
      Long authorID,
      String role,
      String pattern,
      Pageable pageable,
      Boolean isPublic) {

    checkDataAccess(userId, role, authorID);

    Page<Category> categories;
    if (Boolean.TRUE.equals(isPublic)) {
      categories =
          categoryRepository.findAllByCategorySetIsNullAndAuthorIDAndIsPublicAndNameContainingIgnoreCase(
              authorID, true, pattern.toLowerCase(), pageable);
    } else {
      categories =
          categoryRepository.findAllByCategorySetIsNullAndAuthorIDAndNameContainingIgnoreCase(
              authorID, pattern.toLowerCase(), pageable);
    }

    return categories.map(this::mapToDTO);
  }

  @Transactional
  public CategoryDTO create(Long userId, CategoryBody catBody) {
    if (catBody.name() == null || catBody.name().isBlank())
      throw new InvalidRequestException("Category name is required.");

    Type categoryType;
    try {
      categoryType = Type.valueOf(catBody.type());
    } catch (IllegalArgumentException e) {
      throw new InvalidRequestException("Invalid category type: " + catBody.type());
    }
    CategorySet set = null;
    if(catBody.setId() != null)
        set = categorySetRepository.findById(catBody.setId()).orElseThrow(() -> new ResourceNotFoundException("Category set not found with ID: " + catBody.setId()));

    Category category =
        Category.builder()
            .name(catBody.name())
            .authorID(userId)
            .type(categoryType)
            .description(catBody.description())
            .photoUrl(DEFAULT_PHOTO_URL)
            .created(LocalDateTime.now())
                .categorySet(set)
            .views(0)
            .isPublic(catBody.isPublic())
            .build();

    Category savedCategory = categoryRepository.save(category);

    if(set != null){
        set.add(savedCategory);
        categorySetRepository.save(set);
    }

    return mapToDTO(savedCategory);
  }

  @Transactional
  public void addImage(Long userId, String role, Long id, MultipartFile image)
      throws FileUploadException {
    Category category = findById(id);

    checkAuthor(userId, role, category);

    String path = fileUploadService.addCategoryImage(image, id);
    category.setPhotoUrl(path);
    categoryRepository.save(category);
  }

  @Transactional
  protected void addImageUrl(Long id, String path) {
    Category category = findById(id);
    category.setPhotoUrl(path);
    categoryRepository.save(category);
  }

  @Transactional
  public void remove(Long userId, String role, Long id)
      throws FileUploadException, MalformedURLException {
    Category category = findById(id);

    checkAuthor(userId, role, category);

    for (Option option : category.getOptions()) {
      String photoUrl = option.getPhotoUrl();
      if (photoUrl != null) {
        fileUploadService.removeImage(photoUrl);
      }
    }

    removeImgInternal(userId, role, id, category);

    categoryRepository.delete(category);
  }

  @Transactional
  public void removeImg(Long userId, String role, Long id)
      throws FileUploadException, MalformedURLException {
    Category category = findById(id);
    removeImgInternal(userId, role, id, category);
  }

  private void removeImgInternal(Long userId, String role, Long id, Category category)
      throws FileUploadException, MalformedURLException {

    checkAuthor(userId, role, category);

    String filePath = category.getPhotoUrl();

    category.setPhotoUrl(null);
    categoryRepository.save(category);

    if (filePath != null && !filePath.equals(DEFAULT_PHOTO_URL)) {
      fileUploadService.removeImage(filePath);
    }
  }

  @Transactional
  public void increaseViews(Long id) {
    Category category = findById(id);
    category.setViews(category.getViews() + 1);
    categoryRepository.save(category);
  }

  @Transactional
  public void updateCategory(Long userId, String role, Long id, CategoryBody categoryBody) {
    Category category = findById(id);

    checkAuthor(userId, role, category);

    if (categoryBody.name() != null) {
      category.setName(categoryBody.name());
    }
    if (categoryBody.type() != null) {
      try {
        Type type = Type.valueOf(categoryBody.type());
        category.setType(type);
      } catch (IllegalArgumentException e) {
        throw new InvalidRequestException("Invalid category type: " + categoryBody.type());
      }
    }
    if (categoryBody.description() != null) {
      category.setDescription(categoryBody.description());
    }
    if (categoryBody.isPublic() != null) {
      category.setIsPublic(categoryBody.isPublic());
    }

    categoryRepository.save(category);
  }

  protected void checkAuthor(Long userId, String role, Category category) {
    if (role.equals("ADMIN")) return;
    if (!userId.equals(category.getAuthorID())) {
      throw new ForbiddenAccessException("You are not allowed to modify this category.");
    }
  }

  protected CategoryDTO mapToDTO(Category category) {
    return new CategoryDTO(
        category.getId(),
        category.getName(),
        category.getAuthorID(),
        category.getType(),
        category.getPhotoUrl(),
        category.getDescription(),
        category.getViews(),
        category.getCreated(),
        category.getIsPublic());
  }

  private void checkDataAccess(Long userId, String role, Long authorID) {
    if (!role.equals("ADMIN") && !userId.equals(authorID)) {
      throw new ForbiddenAccessException(
          "Access denied. Provided user ID doesn't match the author ID.");
    }
  }
}
