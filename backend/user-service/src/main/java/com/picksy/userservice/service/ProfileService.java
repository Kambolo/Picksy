package com.picksy.userservice.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.picksy.UserProfileMessage;
import com.picksy.userservice.exception.FileUploadException;
import com.picksy.userservice.model.Profile;
import com.picksy.userservice.repository.ProfileRepository;
import com.picksy.userservice.response.ProfileDTO;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileService {
  private final ProfileRepository profileRepository;
  private final String AVATAR_PATH = "/avatar";
  private final Cloudinary cloudinary;
  private final String TOPIC = "register-user";
  private final String DEFAULT_AVATAR_PATH = "https://res.cloudinary.com/dctiucda1/image/upload/v1764165461/default_wm1msb.png";


  @Transactional
  @KafkaListener(topics = TOPIC, groupId = "user-auth")
  public void createNewProfile(UserProfileMessage userProfileMessage) {
    String avatarUrl = userProfileMessage.picture();
    Long userId = userProfileMessage.userId();

    String finalAvatarUrl = uploadGoogleAvatarToCloudinary(avatarUrl, userId);

    Profile profile =
        Profile.builder()
            .userId(userProfileMessage.userId())
            .avatarUrl(finalAvatarUrl)
            .bio("")
            .build();
    System.out.println("new profile created with user id: " + userProfileMessage.userId());
    profileRepository.save(profile);
  }

  private String uploadGoogleAvatarToCloudinary(String avatarUrl, Long userId) {
      String finalAvatarUrl = DEFAULT_AVATAR_PATH;
      RestTemplate restTemplate = new RestTemplate();

      if (avatarUrl != null) {
          avatarUrl = avatarUrl.replaceFirst("=s\\d+-c", "=s400");
          try {
              byte[] imageBytes = restTemplate.getForObject(avatarUrl, byte[].class);

              Map options = ObjectUtils.asMap(
                      "public_id", "avatar" + userId, "folder", AVATAR_PATH, "overwrite", true);

              Map uploadResult = cloudinary.uploader().upload(imageBytes, options);
              finalAvatarUrl = uploadResult.get("secure_url").toString();

          } catch (Exception e) {
              System.out.println("Failed to fetch avatar from Google or upload to Cloudinary: " + e.getMessage());
          }
      }
      return finalAvatarUrl;
  }

  @Transactional
  public void changeBio(Long id, String newBio) throws BadRequestException {
    Profile profile =
        profileRepository
            .findByUserId(id)
            .orElseThrow(() -> new BadRequestException("User not found."));
    if (newBio.length() > 500)
      throw new BadRequestException(
          "Max BIO length is 500. Requested BIO length is - " + newBio.length());
    profile.setBio(newBio);
    profileRepository.save(profile);
  }

  @Transactional
  public String changeAvatar(MultipartFile image, Long id) throws FileUploadException {
    Profile profile =
        profileRepository
            .findByUserId(id)
            .orElseThrow(() -> new BadRequestException("User not found."));
    String path = uploadPhoto(AVATAR_PATH, image, id, "avatar");
    profile.setAvatarUrl(path);
    profileRepository.save(profile);
    return path;
  }

  @Transactional
  public void deleteAvatar(Long id)
      throws MalformedURLException, FileUploadException, BadRequestException {
    Optional<Profile> profileOptional = profileRepository.findByUserId(id);

    if (profileOptional.isEmpty())
      throw new BadRequestException("Avatar with user id - " + id + " does not exist.");

    Profile profile = profileOptional.get();

    String filePath = profile.getAvatarUrl();

    profile.setAvatarUrl(null);
    profileRepository.save(profile);

    removeImage(filePath);
  }

  public ProfileDTO findByUserId(Long id) {
    Optional<Profile> profileOptional = profileRepository.findById(id);

    if (profileOptional.isEmpty())
      throw new BadRequestException("Avatar with id - " + id + " does not exist.");

    Profile profile = profileOptional.get();
    return new ProfileDTO(profile.getUserId(), profile.getAvatarUrl(), profile.getBio());
  }

  private String uploadPhoto(String uploadDir, MultipartFile photo, Long id, String filePrefix)
      throws FileUploadException {

    List<String> allowed = Arrays.asList("jpg", "jpeg", "png");

    String filename = photo.getOriginalFilename();
    String extension = getExtension(filename);

    if (!allowed.contains(extension)) {
      throw new FileUploadException("Allowed file extensions: " + allowed);
    }

    filename = filePrefix + id;

    Map options =
        ObjectUtils.asMap(
            "public_id", filename,
            "asset_folder", uploadDir,
            "overwrite", true);

    try {
      Map uploadResult = cloudinary.uploader().upload(photo.getBytes(), options);
      return uploadResult.get("secure_url").toString();
    } catch (Exception e) {
      throw new FileUploadException("Upload file error: " + e.getMessage());
    }
  }

  private String getExtension(String filename) {
    if (filename == null || !filename.contains(".")) {
      return null;
    }
    return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
  }

  public void removeImage(String url) throws MalformedURLException, FileUploadException {
    String publicId = extractPublicIdFromURL(url);
    try {
      cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    } catch (Exception e) {
      throw new FileUploadException("Image remove error: " + e.getMessage());
    }
  }

  private String extractPublicIdFromURL(String url) throws MalformedURLException {

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
      return withExtension.replaceAll("\\.(jpg|jpeg|png|gif)$", ""); // remove extension

    } catch (URISyntaxException e) {
      throw new MalformedURLException("Malformed URL " + e.getMessage());
    }
  }

  public List<ProfileDTO> getProfilesByUserIds(List<Long> ids) {
    return profileRepository.findAllById(ids).stream()
        .map(
            (profile) ->
                new ProfileDTO(profile.getUserId(), profile.getAvatarUrl(), profile.getBio()))
        .collect(Collectors.toList());
  }
}
