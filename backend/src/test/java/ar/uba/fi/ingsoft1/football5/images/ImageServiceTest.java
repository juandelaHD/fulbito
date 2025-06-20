package ar.uba.fi.ingsoft1.football5.images;

import ar.uba.fi.ingsoft1.football5.common.exception.ItemNotFoundException;
import ar.uba.fi.ingsoft1.football5.config.security.JwtUserDetails;
import ar.uba.fi.ingsoft1.football5.fields.Field;
import ar.uba.fi.ingsoft1.football5.fields.FieldRepository;
import ar.uba.fi.ingsoft1.football5.fields.GrassType;
import ar.uba.fi.ingsoft1.football5.fields.Location;
import ar.uba.fi.ingsoft1.football5.teams.TeamRepository;
import ar.uba.fi.ingsoft1.football5.user.User;
import ar.uba.fi.ingsoft1.football5.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static ar.uba.fi.ingsoft1.football5.user.Role.USER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @Mock
    private ImageRepository imageRepository;

    @InjectMocks
    private ImageService imageService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FieldRepository fieldRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private JwtUserDetails userDetails;

    @Mock
    private User owner;

    private String storagePath;

    @BeforeEach
    void setUp() throws URISyntaxException {
        Path defaultImgPath = Paths.get(Objects.requireNonNull(getClass().getClassLoader()
                .getResource("default_profile.webp")).toURI());
        storagePath = defaultImgPath.getParent().toString();

        imageService = new ImageService(storagePath, imageRepository, userRepository, fieldRepository, teamRepository);

        AvatarImage.injectRepository(userRepository);
        FieldImage.injectRepositories(fieldRepository, userRepository);
    }

    @Test
    void saveFieldImages_whenFieldImagesIsNull_doNotSaveAnything() throws IOException {
        Field field = new Field(1L, "field 1", GrassType.NATURAL_GRASS, true,
                new Location("zone b", "address 2"), owner);

        imageService.saveFieldImages(field, null);
        assertTrue(field.getImages().isEmpty());
    }

    @Test
    void saveFieldImages_whenFieldImagesIsEmpty_doNotSaveAnything() throws IOException {
        Field field = new Field(1L, "field 1", GrassType.NATURAL_GRASS, true,
                new Location("zone b", "address 2"), owner);

        imageService.saveFieldImages(field, List.of());
        assertTrue(field.getImages().isEmpty());
    }

    @Test
    void saveFieldImages_whenOneFieldImage_saveFieldImage() throws IOException {
        Field field = new Field(1L, "field 1", GrassType.NATURAL_GRASS, true,
                new Location("zone b", "address 2"), owner);

        byte[] imageData = new byte[]{1, 2, 3};
        MultipartFile file = new MockMultipartFile("file", imageData);

        FieldImage image = new FieldImage(imageData, field);
        image.setId(1L);
        when(imageRepository.save(any())).thenReturn(image);

        imageService.saveFieldImages(field, List.of(file));

        assertEquals(1, field.getImages().size());

        FieldImage firstImage = field.getImages().getFirst();
        assertEquals(image, firstImage);
    }

    @Test
    void saveFieldImages_whenManyFieldImages_saveFieldImages() throws IOException {
        Field field = new Field(1L, "field 1", GrassType.NATURAL_GRASS, true,
                new Location("zone b", "address 2"), owner);

        byte[] imageData1 = new byte[]{1, 2, 3};
        byte[] imageData2 = new byte[]{4, 5, 6};

        MultipartFile file1 = new MockMultipartFile("file1", imageData1);
        MultipartFile file2 = new MockMultipartFile("file2", imageData2);

        FieldImage image1 = new FieldImage(imageData1, field);
        image1.setId(1L);

        FieldImage image2 = new FieldImage(imageData2, field);
        image2.setId(2L);

        when(imageRepository.save(any(Image.class)))
                .thenReturn(image1)
                .thenReturn(image2);

        imageService.saveFieldImages(field, List.of(file1, file2));

        assertEquals(2, field.getImages().size());

        FieldImage firstImage = field.getImages().getFirst();
        assertEquals(image1, firstImage);

        FieldImage secondImage = field.getImages().get(1);
        assertEquals(image2, secondImage);
    }

    @Test
    void saveAvatarImage_whenAvatarImageIsNull_saveDefaultAvatarImage() throws IOException {
        User user = new User("test-user", "test", "test", "F", "Zone", 22, "", USER);
        Path pathImg = Paths.get(storagePath, "default_profile.webp");
        byte[] data = Files.readAllBytes(pathImg);

        AvatarImage avatarImage = new AvatarImage(data, user);

        when(imageRepository.save(any())).thenReturn(avatarImage);

        imageService.saveAvatarImage(user, null);

        assertEquals(avatarImage, user.getAvatar());
    }

    @Test
    void saveAvatarImage_whenAvatarImage_saveAvatarImage() throws IOException {
        User user = new User("test-user", "test", "test", "F", "Zone", 22, "", USER);

        byte[] imageData = new byte[]{1, 2, 3};
        MultipartFile file = new MockMultipartFile("file", imageData);

        AvatarImage avatar = new AvatarImage(imageData, user);
        avatar.setId(1L);
        when(imageRepository.save(any())).thenReturn(avatar);

        imageService.saveAvatarImage(user, file);

        assertEquals(avatar, user.getAvatar());
    }

    @Test
     void getImageData_whenFieldImageExists_returnsFieldImageData() throws ItemNotFoundException {
        Field field = new Field(1L, "field 1", GrassType.NATURAL_GRASS, true,
                new Location("zone b", "address 2"), owner);

        byte[] imageData = new byte[]{1, 2, 3};
        FieldImage image = new FieldImage(imageData, field);
        image.setId(1L);

        when(imageRepository.findById(anyLong())).thenReturn(Optional.of(image));

        byte[] result = imageService.getImageData(1L);

        assertArrayEquals(imageData, result);
    }

    @Test
    void getImageData_whenAvatarImageExists_returnsAvatarImageData() throws ItemNotFoundException {
        User user = new User("test-user", "test", "test", "F", "Zone", 22, "", USER);

        byte[] imageData = new byte[]{1, 2, 3};
        AvatarImage image = new AvatarImage(imageData, user);
        image.setId(1L);

        when(imageRepository.findById(anyLong())).thenReturn(Optional.of(image));

        byte[] result = imageService.getImageData(1L);

        assertArrayEquals(imageData, result);
    }

    @Test
    void getImageData_whenImageDoesNotExist_throwsItemNotFoundException() {
        when(imageRepository.findById(anyLong())).thenReturn(Optional.empty());

        ItemNotFoundException exception = assertThrows(ItemNotFoundException.class, () ->
            imageService.getImageData(1L)
        );

        assertEquals("Failed to find image with id '1'", exception.getMessage());
    }

    @Test
    void deleteImage_whenImageNotFound_throwsItemNotFoundException() {
        when(imageRepository.findById(anyLong())).thenReturn(Optional.empty());

        ItemNotFoundException exception = assertThrows(ItemNotFoundException.class, () ->
            imageService.deleteImage(1L, userDetails)
        );

        assertEquals("Failed to find image with id '1'", exception.getMessage());
    }

    @Test
    void deleteImage_whenAvatarImageExists_deletesAvatarImage() throws ItemNotFoundException {
        User user = new User("test-user", "test", "test", "F", "Zone", 22, "", USER);

        AvatarImage image = new AvatarImage(new byte[]{1, 2, 3}, user);
        image.setId(1L);

        when(imageRepository.findById(anyLong())).thenReturn(Optional.of(image));

        when(userDetails.username()).thenReturn("test-user");
        when(userRepository.findByUsername("test-user")).thenReturn(Optional.of(user));

        imageService.deleteImage(1L, userDetails);
        verify(imageRepository).delete(image);
    }

    @Test
    void deleteImage_whenFieldImageExists_deletesFieldImage() throws ItemNotFoundException {
        Field field = new Field(1L, "field 1", GrassType.NATURAL_GRASS, true,
                new Location("zone b", "address 2"), owner);

        FieldImage image = new FieldImage(new byte[]{1, 2, 3}, field);
        image.setId(1L);

        when(imageRepository.findById(anyLong())).thenReturn(Optional.of(image));

        when(userDetails.username()).thenReturn("test-user");
        when(owner.getUsername()).thenReturn("test-user");
        when(userRepository.findByUsername("test-user")).thenReturn(Optional.of(owner));

        when(fieldRepository.findById(1L)).thenReturn(Optional.of(field));

        imageService.deleteImage(1L, userDetails);
        verify(imageRepository).delete(image);
    }
}
