package com.picksy.categoryservice.service;

import com.picksy.categoryservice.exception.FileUploadException;
import com.picksy.categoryservice.model.Category;
import com.picksy.categoryservice.model.Option;
import com.picksy.categoryservice.repositoty.CategoryRepository;
import com.picksy.categoryservice.repositoty.OptionRepository;
import com.picksy.categoryservice.request.OptionBody;
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

    public Option findById(Long id) throws BadRequestException {
        return optionRepository.findById(id).orElseThrow(() -> new BadRequestException("Option not found"));
    }

    public List<OptionDTO> findAllForCategory(Long catId) throws BadRequestException {
        Category category = categoryService.findById(catId);
        return optionRepository.findAllByCategory(category)
                .stream()
                .map(option -> new OptionDTO(
                        option.getId(),
                        option.getName(),
                        option.getPhotoUrl()
                )).toList();
    }

    @Transactional
    public void add(OptionBody optionBody) throws BadRequestException {
        Category category = categoryService.findById(optionBody.categoryId());

        Option option = Option.builder()
                .name(optionBody.name())
                .build();

        category.add(option);
        categoryRepository.save(category);
    }

    @Transactional
    public void addImage(Long id, MultipartFile image) throws BadRequestException, FileUploadException {
        Option option = optionRepository.findById(id).orElseThrow(() -> new BadRequestException("Option not found"));
        String path = fileUploadService.addOptionImage(image, id);
        option.setPhotoUrl(path);
        optionRepository.save(option);
    }

    @Transactional
    public void remove(Long id) throws BadRequestException, FileUploadException, MalformedURLException {
        Option option = findById(id);
        Category category = option.getCategory();

        try{
            category.remove(option);
        }catch(Exception e){
            throw new BadRequestException("Option can't be removed.");
        }

        removeImg(id);

        categoryRepository.save(category);
    }

    @Transactional
    public void removeImg(Long id) throws BadRequestException, FileUploadException, MalformedURLException {
        Option option = findById(id);
        String path = option.getPhotoUrl();
        option.setPhotoUrl(null);
        fileUploadService.removeImage(path);
        optionRepository.save(option);
    }

    @Transactional
    public void update(Long id, String name) throws BadRequestException {
        Option option = findById(id);

        try{
            option.setName(name);
        } catch (Exception e) {
            throw new BadRequestException("Option update fail.");
        }
    }
}
