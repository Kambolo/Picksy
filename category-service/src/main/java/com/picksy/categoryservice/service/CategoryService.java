package com.picksy.categoryservice.service;

import com.picksy.categoryservice.exception.FileUploadException;
import com.picksy.categoryservice.model.Category;
import com.picksy.categoryservice.model.Option;
import com.picksy.categoryservice.repositoty.CategoryRepository;
import com.picksy.categoryservice.request.CategoryBody;
import com.picksy.categoryservice.response.CategoryDTO;
import com.picksy.categoryservice.util.enums.Type;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final FileUploadService fileUploadService;

    public CategoryDTO findDTOById(Long id) throws BadRequestException{
        Category category = findById(id);
        return new CategoryDTO(category.getId(), category.getName(), category.getAuthor(), category.getType(), category.getPhotoUrl());
    }

    Category findById(Long id) throws BadRequestException {
        return categoryRepository.findById(id).orElseThrow(() -> new BadRequestException("Category not found"));
    }

    public Page<CategoryDTO> findAll(Pageable pageable){
        return categoryRepository.findAll(pageable)
                .map( category -> new CategoryDTO(
                        category.getId(),
                        category.getName(),
                        category.getAuthor(),
                        category.getType(),
                        category.getPhotoUrl()));
    }

    public Page<CategoryDTO> findAllByAuthor(Pageable pageable, String author) throws BadRequestException {
        Page<Category> categories = categoryRepository.findAllByAuthorIgnoreCase(author, pageable);

        if (categories.isEmpty()) {
            throw new BadRequestException("Category not found.");
        }

        return categories.map(category -> new CategoryDTO(
                category.getId(),
                category.getName(),
                category.getAuthor(),
                category.getType(),
                category.getPhotoUrl()
        ));
    }

    @Transactional
    public CategoryDTO create(CategoryBody catBody) throws BadRequestException {
        try{
            Category category = Category.builder()
                    .name(catBody.name())
                    .author(catBody.author())
                    .type(Type.valueOf(catBody.type()))
                    .build();

            Category savedCategory = categoryRepository.save(category);

            return new CategoryDTO(savedCategory.getId(), savedCategory.getName(), savedCategory.getAuthor(), savedCategory.getType(), null);
        }catch (Exception e){
            throw new BadRequestException("Bad category body.");
        }
    }

    @Transactional
    public void addImage(Long id, MultipartFile image) throws BadRequestException, FileUploadException {
        Category category = findById(id);
        String path = fileUploadService.addCategoryImage(image, id);
        category.setPhotoUrl(path);
        categoryRepository.save(category);
    }


    @Transactional
    public void remove(Long id) throws BadRequestException, FileUploadException, MalformedURLException {
        Category category = findById(id);

        // Remove all option images manually
        for (Option option : category.getOptions()) {
            String photoUrl = option.getPhotoUrl();
            if (photoUrl != null) {
                fileUploadService.removeImage(photoUrl);
            }
        }

        removeImg(id);

        categoryRepository.delete(findById(id));
    }

    @Transactional
    public void removeImg(Long id) throws BadRequestException, FileUploadException, MalformedURLException {
        Category category = findById(id);

        String filePath = category.getPhotoUrl();

        category.setPhotoUrl(null);
        categoryRepository.save(category);

        fileUploadService.removeImage(filePath);
    }

    @Transactional
    public void update(Long id, CategoryBody categoryBody) throws BadRequestException {
        Category category = findById(id); // get existing category

        if (categoryBody.name() != null) {
            category.setName(categoryBody.name());
        }

        if (categoryBody.author() != null) {
            category.setAuthor(categoryBody.author());
        }

        if (categoryBody.type() != null) {
            try {
                Type type = Type.valueOf(categoryBody.type());
                category.setType(type);
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid category type: " + categoryBody.type());
            }
        }

        categoryRepository.save(category);
    }
}
