package com.picksy.categoryservice.service;

import com.picksy.categoryservice.exception.FileUploadException;
import com.picksy.categoryservice.model.Category;
import com.picksy.categoryservice.model.Option;
import com.picksy.categoryservice.repository.CategoryRepository;
import com.picksy.categoryservice.repository.OptionRepository;
import com.picksy.categoryservice.request.OptionBody;
import com.picksy.categoryservice.response.CategoryDTO;
import com.picksy.categoryservice.response.CategoryWithOptionsDTO;
import com.picksy.categoryservice.response.OptionDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OptionService {

  private final OptionRepository optionRepository;
  private final CategoryRepository categoryRepository;
  private final CategoryService categoryService;
  private final FileUploadService fileUploadService;

  private final String DEFAULT_PHOTO_URL =
      "https://res.cloudinary.com/dctiucda1/image/upload/v1764698183/image_zaopfn.png";

  public Option findById(Long id) throws BadRequestException {
    return optionRepository
        .findById(id)
        .orElseThrow(() -> new BadRequestException("Option not found"));
  }

  public List<OptionDTO> findAllForCategory(Long catId) throws BadRequestException {
    Category category = categoryService.findById(catId);
    if (category == null) {
      throw new BadRequestException("Category not found");
    }
    return optionRepository.findAllByCategory(category).stream()
        .map(option -> new OptionDTO(option.getId(), option.getName(), option.getPhotoUrl()))
        .toList();
  }

  @Transactional
  public OptionDTO add(Long userId, String role, OptionBody optionBody) throws BadRequestException {
    Category category = categoryService.findById(optionBody.categoryId());
    categoryService.checkAuthor(userId, role, category);

    if (category == null) {
      throw new BadRequestException("Category not found");
    }

    Option option =
        Option.builder()
            .name(optionBody.name())
            .photoUrl(DEFAULT_PHOTO_URL)
            .build();

    category.add(option);
    optionRepository.save(option);

    categoryRepository.save(category);

    return new OptionDTO(option.getId(), option.getName(), option.getPhotoUrl());
  }

  @Transactional
  public void addImage(Long userId, String role, Long id, MultipartFile image)
      throws BadRequestException, FileUploadException {
    Option option =
        optionRepository
            .findById(id)
            .orElseThrow(() -> new BadRequestException("Option not found"));

    categoryService.checkAuthor(userId, role, option.getCategory());

    String path = fileUploadService.addOptionImage(image, id);
    option.setPhotoUrl(path);
    optionRepository.save(option);
  }

  @Transactional
  public void addImageUrl(Long id, String path) throws BadRequestException {
    Option option =
        optionRepository
            .findById(id)
            .orElseThrow(() -> new BadRequestException("Option not found"));

    option.setPhotoUrl(path);
    optionRepository.save(option);
  }

  @Transactional
  public void remove(Long userId, String role, Long id)
      throws BadRequestException, FileUploadException, MalformedURLException {
    Option option = findById(id);

    Category category = option.getCategory();
    categoryService.checkAuthor(userId, role, category);

    try {
      category.remove(option);
    } catch (Exception e) {
      throw new BadRequestException("Option can't be removed.");
    }

    removeImg(userId, role, id);

    categoryRepository.save(category);
  }

  @Transactional
  public void removeImg(Long userId, String role, Long id)
      throws BadRequestException, FileUploadException, MalformedURLException {
    Option option = findById(id);

    categoryService.checkAuthor(userId, role, option.getCategory());

    String path = option.getPhotoUrl();
    option.setPhotoUrl(DEFAULT_PHOTO_URL);
    fileUploadService.removeImage(path);
    optionRepository.save(option);
  }

  @Transactional
  public void update(Long userId, String role, Long id, String name) throws BadRequestException {
    Option option = findById(id);

    categoryService.checkAuthor(userId, role, option.getCategory());

    try {
      option.setName(name);
    } catch (Exception e) {
      throw new BadRequestException("Option update fail.");
    }

    optionRepository.save(option);
  }

  public CategoryWithOptionsDTO findCategoryWithOptions(Long id) throws BadRequestException {
    CategoryDTO category = categoryService.findDTOById(id);
    List<OptionDTO> options = findAllForCategory(id);

    return new CategoryWithOptionsDTO(category, options);
  }
}
