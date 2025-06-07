package ar.uba.fi.ingsoft1.football5.images;

import ar.uba.fi.ingsoft1.football5.common.exception.ItemNotFoundException;
import ar.uba.fi.ingsoft1.football5.config.security.JwtUserDetails;
import ar.uba.fi.ingsoft1.football5.fields.Field;
import ar.uba.fi.ingsoft1.football5.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@Transactional
public class ImageService {

    private final ImageRepository imageRepository;
    private final AvatarImageService avatarImageService;
    private final FieldImageService fieldImageService;

    @Value("${app.images.path}")
    private String storagePath;

    public ImageService(ImageRepository imageRepository, AvatarImageService avatarImageService, FieldImageService fieldImageService) {
        this.imageRepository = imageRepository;
        this.avatarImageService = avatarImageService;
        this.fieldImageService = fieldImageService;
    }

    public void saveImages(Field field, List<MultipartFile> images) throws IOException {
        if (images != null) {
            for (MultipartFile file : images) {
                byte[] data = file.getBytes();
                Image image = new Image(data, field);
                image = imageRepository.save(image);
                field.getImages().add(image);
            }
        }
    }

    public void saveImage(User user, MultipartFile file) throws IOException {
        byte[] data;

        if (file == null || file.isEmpty()) {
            Path pathImg = Paths.get(storagePath, "default_profile.webp");
            data = Files.readAllBytes(pathImg);
        } else {
            data = file.getBytes();
        }

        Image image = new Image(data, user);
        image = imageRepository.save(image);
        user.setAvatar(image);
    }

    public byte[] getImageData(Long id) throws ItemNotFoundException {
        if (id == null) {
            throw new ItemNotFoundException("image", id);
        }
        return imageRepository.findById(id)
                .map(Image::getData)
                .orElseThrow(() -> new ItemNotFoundException("image", id));
    }

    public void deleteImage(Long id, JwtUserDetails userDetails) throws ItemNotFoundException {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("image", id));

        avatarImageService.validateAvatarOwnership(image, userDetails);
        fieldImageService.validateFieldImageOwnership(image, userDetails);

        imageRepository.delete(image);
    }
}
