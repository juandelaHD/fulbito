package ar.uba.fi.ingsoft1.football5.images;

import ar.uba.fi.ingsoft1.football5.common.exception.ItemNotFoundException;
import ar.uba.fi.ingsoft1.football5.config.security.JwtUserDetails;
import ar.uba.fi.ingsoft1.football5.fields.Field;
import ar.uba.fi.ingsoft1.football5.fields.FieldRepository;
import ar.uba.fi.ingsoft1.football5.teams.Team;
import ar.uba.fi.ingsoft1.football5.teams.TeamRepository;
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

    private static final String IMAGE_ITEM = "image";
    private static final String DEFAULT_IMAGE = "default_profile.webp";

    private final String storagePath;
    private final ImageRepository imageRepository;
    private final UserRepository userRepository;
    private final FieldRepository fieldRepository;
    private final TeamRepository teamRepository;

    @PostConstruct
    public void injectRepositories() {
        AvatarImage.injectRepository(userRepository);
        FieldImage.injectRepositories(fieldRepository, userRepository);
        TeamImage.injectRepository(teamRepository);
    }

    public ImageService(@Value("${app.images.path}") String storagePath, ImageRepository imageRepository,
                        UserRepository userRepository, FieldRepository fieldRepository,
                        TeamRepository teamRepository) {
        this.storagePath = storagePath;
        this.imageRepository = imageRepository;
        this.userRepository = userRepository;
        this.fieldRepository = fieldRepository;
        this.teamRepository = teamRepository;
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
            Path pathImg = Paths.get(storagePath, DEFAULT_IMAGE);
            data = Files.readAllBytes(pathImg);
        } else {
            data = file.getBytes();
        }

        AvatarImage image = new AvatarImage(data, user);
        image = imageRepository.save(image);
        user.setAvatar(image);
    }

    public void saveTeamImage(Team team, MultipartFile file) throws IOException {
        byte[] data;

        if (file == null || file.isEmpty()) {
            Path pathImg = Paths.get(storagePath, DEFAULT_IMAGE);
            data = Files.readAllBytes(pathImg);
        } else {
            data = file.getBytes();
        }

        TeamImage image = new TeamImage(data, team);
        image = imageRepository.save(image);
        team.setImage(image);
    }

    public byte[] getImageData(Long id) throws ItemNotFoundException {
        return imageRepository.findById(id)
                .map(Image::getData)
                .orElseThrow(() -> new ItemNotFoundException(IMAGE_ITEM, id));
    }

    public void deleteImage(Long id, JwtUserDetails userDetails) throws ItemNotFoundException {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(IMAGE_ITEM, id));

        image.validateOwnership(userDetails);
        imageRepository.delete(image);
    }
}
