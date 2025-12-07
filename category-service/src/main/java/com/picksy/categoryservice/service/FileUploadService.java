package com.picksy.categoryservice.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.picksy.categoryservice.exception.FileUploadException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final String CAT_IMG_PATH = "category/";
    private final String OPT_IMG_PATH = "option/";
    private final Cloudinary cloudinary;

    public String addCategoryImage(MultipartFile image, Long id) throws FileUploadException {
        String path = uploadPhoto(CAT_IMG_PATH, image, id, "cat");
        return path;
    }

    public String addOptionImage(MultipartFile image, Long id) throws FileUploadException {
        String path = uploadPhoto(OPT_IMG_PATH, image, id, "opt");
        return path;
    }

    public void removeImage(String url) throws MalformedURLException, FileUploadException {
        String publicId = extractPublicIdFromURL(url);
        try{
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        }catch(Exception e){
            throw new FileUploadException("Image remove error: " + e.getMessage());
        }

    }

    public String extractPublicIdFromURL(String url) throws MalformedURLException {
        try {
            URI uri = new URI(url);
            String path = uri.getPath();

            int uploadIndex = path.indexOf("/upload/");
            if (uploadIndex == -1) throw new IllegalArgumentException("Invalid Cloudinary URL");

            String afterUpload = path.substring(uploadIndex + "/upload/".length());

            String[] parts = afterUpload.split("/");
            if (parts[0].startsWith("v")) {
                parts = Arrays.copyOfRange(parts, 1, parts.length);
            }


            String withExtension = String.join("/", parts);
            return withExtension.replaceAll("\\.(jpg|jpeg|png|gif)$", "");

        } catch (URISyntaxException e) {
            throw new MalformedURLException("Malformed URL " + e.getMessage());
        }
    }

    private String uploadPhoto(String uploadDir, MultipartFile photo, Long id, String filePrefix) throws FileUploadException {

        List<String> allowed = Arrays.asList("jpg", "jpeg", "png");

        String filename = photo.getOriginalFilename();
        String extension = getExtension(filename);

        if(!allowed.contains(extension)){
            throw new FileUploadException("Allowed file extensions: " + allowed);
        }

        filename = filePrefix + id;

        Map options = ObjectUtils.asMap(
                "public_id", filename,
                "asset_folder", uploadDir,
                "overwrite", true
        );

        try{
            Map uploadResult = cloudinary.uploader().upload(photo.getBytes(), options);
            return uploadResult.get("secure_url").toString();
        }catch(Exception e){
            throw new FileUploadException("Upload file error: " + e.getMessage());
        }
    }

    private boolean hasValidExtension(String filename, List<String> allowedExtensions) {
        if (filename == null || !filename.contains(".")) {
            return false;
        }
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        return allowedExtensions.contains(extension);
    }

    private String getExtension(String filename){
        if (filename == null || !filename.contains(".")) {
            return null;
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    public String buildResizedImageUrl(String publicId, int width, int height) {
        return cloudinary.url()
                .secure(true)
                .transformation(new Transformation().width(width).height(height).crop("fill"))
                .generate(publicId);
    }
}
