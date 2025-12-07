package com.picksy.categoryservice.service;

import com.picksy.categoryservice.exception.FileUploadException;
import com.picksy.categoryservice.exception.ForbiddenAccessException;
import com.picksy.categoryservice.exception.InvalidRequestException;
import com.picksy.categoryservice.exception.ResourceNotFoundException;
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

    public Option findById(Long id) {
        return optionRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Option not found with ID: " + id));
    }

    public List<OptionDTO> findAllForCategory(Long catId) {
        Category category = categoryService.findById(catId);

        return optionRepository.findAllByCategory(category).stream()
                .map(option -> new OptionDTO(option.getId(), option.getName(), option.getPhotoUrl()))
                .toList();
    }

    @Transactional
    public OptionDTO add(Long userId, String role, OptionBody optionBody) {
        if (optionBody.name() == null || optionBody.name().isBlank()) {
            throw new InvalidRequestException("Option name is required.");
        }

        Category category = categoryService.findById(optionBody.categoryId());
        categoryService.checkAuthor(userId, role, category);

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
            throws FileUploadException {
        Option option = findById(id);

        categoryService.checkAuthor(userId, role, option.getCategory());

        String path = fileUploadService.addOptionImage(image, id);
        option.setPhotoUrl(path);
        optionRepository.save(option);
    }

    @Transactional
    public void addImageUrl(Long id, String path) {
        Option option = findById(id);

        option.setPhotoUrl(path);
        optionRepository.save(option);
    }

    @Transactional
    public void remove(Long userId, String role, Long id)
            throws FileUploadException, MalformedURLException {
        Option option = findById(id);

        Category category = option.getCategory();
        categoryService.checkAuthor(userId, role, category);

        try {
            category.remove(option);
        } catch (Exception e) {
            throw new InvalidRequestException("Option can't be removed from the category: " + e.getMessage());
        }

        removeImgInternal(userId, role, id, option);

        categoryRepository.save(category);
    }

    @Transactional
    protected void removeImgInternal(Long userId, String role, Long id, Option option)
            throws FileUploadException, MalformedURLException {

        categoryService.checkAuthor(userId, role, option.getCategory());

        String path = option.getPhotoUrl();
        option.setPhotoUrl(DEFAULT_PHOTO_URL);

        if (path != null && !path.equals(DEFAULT_PHOTO_URL)) {
            fileUploadService.removeImage(path);
        }
        optionRepository.save(option);
    }

    @Transactional
    public void removeImg(Long userId, String role, Long id)
            throws FileUploadException, MalformedURLException {
        Option option = findById(id);
        removeImgInternal(userId, role, id, option);
    }

    @Transactional
    public void update(Long userId, String role, Long id, String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidRequestException("New option name is required for update.");
        }

        Option option = findById(id);

        categoryService.checkAuthor(userId, role, option.getCategory());

        try {
            option.setName(name);
        } catch (Exception e) {
            throw new InvalidRequestException("Option update failed: " + e.getMessage());
        }

        optionRepository.save(option);
    }

    public CategoryWithOptionsDTO findCategoryWithOptions(Long id) {
        CategoryDTO category = categoryService.findDTOById(id);
        List<OptionDTO> options = findAllForCategory(id);

        return new CategoryWithOptionsDTO(category, options);
    }
}