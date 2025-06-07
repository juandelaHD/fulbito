package ar.uba.fi.ingsoft1.football5.images;

import ar.uba.fi.ingsoft1.football5.common.exception.ItemNotFoundException;
import ar.uba.fi.ingsoft1.football5.config.security.JwtUserDetails;
import ar.uba.fi.ingsoft1.football5.fields.Field;
import ar.uba.fi.ingsoft1.football5.fields.FieldRepository;
import ar.uba.fi.ingsoft1.football5.user.User;
import ar.uba.fi.ingsoft1.football5.user.UserRepository;
import jakarta.annotation.PostConstruct;
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

    private final String storagePath;
    private final ImageRepository imageRepository;
    private final UserRepository userRepository;
    private final FieldRepository fieldRepository;

    @PostConstruct
    public void injectRepositories() {
        AvatarImage.injectRepository(userRepository);
        FieldImage.injectRepositories(fieldRepository, userRepository);
    }

    public ImageService(@Value("${app.images.path}") String storagePath, ImageRepository imageRepository, UserRepository userRepository, FieldRepository fieldRepository) {
        this.storagePath = storagePath;
        this.imageRepository = imageRepository;
        this.userRepository = userRepository;
        this.fieldRepository = fieldRepository;
    }

    public void saveFieldImages(Field field, List<MultipartFile> images) throws IOException {
        if (images != null) {
            for (MultipartFile file : images) {
                byte[] data = file.getBytes();
                FieldImage image = new FieldImage(data, field);
                image = imageRepository.save(image);
                field.getImages().add(image);
            }
        }
    }

    public void saveAvatarImage(User user, MultipartFile file) throws IOException {
        byte[] data;

        if (file == null || file.isEmpty()) {
            Path pathImg = Paths.get(storagePath, "default_profile.webp");
            data = Files.readAllBytes(pathImg);
        } else {
            data = file.getBytes();
        }

        AvatarImage image = new AvatarImage(data, user);
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

        image.validateOwnership(userDetails);

        imageRepository.delete(image);
    }
}
