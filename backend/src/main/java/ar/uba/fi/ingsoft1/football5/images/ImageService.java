package ar.uba.fi.ingsoft1.football5.images;

import ar.uba.fi.ingsoft1.football5.common.exception.ItemNotFoundException;
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

    @Value("${app.images.path}")
    private String storagePath;

    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
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
            Path path_img = Paths.get(storagePath, "default_profile.webp");
            data = Files.readAllBytes(path_img);
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

    public void deleteImage(Long id) throws ItemNotFoundException {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("image", id));
        imageRepository.delete(image);
    }
}
