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
        return new CategoryDTO(category.getId(), category.getName(), category.getAuthorID(), category.getType(), category.getPhotoUrl(), category.getDescription());
    }

    Category findById(Long id) throws BadRequestException {
        return categoryRepository.findById(id).orElseThrow(() -> new BadRequestException("Category not found"));
    }

    public Page<CategoryDTO> findAll(Pageable pageable){
        return categoryRepository.findAll(pageable)
                .map( category -> new CategoryDTO(
                        category.getId(),
                        category.getName(),
                        category.getAuthorID(),
                        category.getType(),
                        category.getPhotoUrl(),
                        category.getDescription()));
    }

    public Page<CategoryDTO> findAllByAuthorID(Pageable pageable, Long authorID) throws BadRequestException {
        Page<Category> categories = categoryRepository.findAllByAuthorID(authorID, pageable);

        if (categories.isEmpty()) {
            throw new BadRequestException("Category not found.");
        }

        return categories.map(category -> new CategoryDTO(
                category.getId(),
                category.getName(),
                category.getAuthorID(),
                category.getType(),
                category.getPhotoUrl(),
                category.getDescription()
        ));
    }

    public Page<CategoryDTO> findBuiltInCategories(Pageable pageable) {
        Page<Category> categories = categoryRepository.findByAuthorIDIsNull(pageable);

        return categories.map(category -> new CategoryDTO(
                category.getId(),
                category.getName(),
                category.getAuthorID(),
                category.getType(),
                category.getPhotoUrl(),
                category.getDescription()
        ));
    }

    public Page<CategoryDTO> findCategoriesByPattern(String pattern, Pageable pageable) {
        Page<Category> categories = categoryRepository.findByNameContaining(pattern, pageable);

        return categories.map(category -> new CategoryDTO(
                category.getId(),
                category.getName(),
                category.getAuthorID(),
                category.getType(),
                category.getPhotoUrl(),
                category.getDescription()
        ));
    }

    @Transactional
    public CategoryDTO create(CategoryBody catBody) throws BadRequestException {

        if(catBody.name() == null || catBody.name().isBlank()) throw new BadRequestException("Category name is required.");

        try{
            Type.valueOf(catBody.type());
        }catch (Exception e){
            throw new BadRequestException("Bad category type.");
        }

        try{
            Category category = Category.builder()
                    .name(catBody.name())
                    .authorID(catBody.author())
                    .type(Type.valueOf(catBody.type()))
                    .description(catBody.description())
                    .build();

            Category savedCategory = categoryRepository.save(category);

            return new CategoryDTO(savedCategory.getId(),
                    savedCategory.getName(),
                    savedCategory.getAuthorID(),
                    savedCategory.getType(),
                    "https://res.cloudinary.com/dctiucda1/image/upload/v1760618779/image_a9gqss.png",
                    savedCategory.getDescription());
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

        categoryRepository.delete(category);
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
            category.setAuthorID(categoryBody.author());
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
