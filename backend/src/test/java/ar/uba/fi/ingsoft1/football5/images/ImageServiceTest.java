package ar.uba.fi.ingsoft1.football5.images;

import ar.uba.fi.ingsoft1.football5.common.exception.ItemNotFoundException;
import ar.uba.fi.ingsoft1.football5.fields.Field;
import ar.uba.fi.ingsoft1.football5.fields.GrassType;
import ar.uba.fi.ingsoft1.football5.fields.Location;
import ar.uba.fi.ingsoft1.football5.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private User owner;

    @InjectMocks
    private ImageService imageService;

    @Test
    void saveImages_whenImagesIsNull_doNotSaveAnything() throws IOException {
        Field field = new Field(1L, "field 1", GrassType.NATURAL_GRASS, true,
                new Location("zone b", "address 2"), owner);

        imageService.saveImages(field, null);

        assertTrue(field.getImages().isEmpty());
    }

    @Test
    void saveImages_whenImagesIsEmpty_doNotSaveAnything() throws IOException {
        Field field = new Field(1L, "field 1", GrassType.NATURAL_GRASS, true,
                new Location("zone b", "address 2"), owner);

        imageService.saveImages(field, List.of());

        assertTrue(field.getImages().isEmpty());
    }

    @Test
    void saveImages_whenOneImage_saveImage() throws IOException {
        Field field = new Field(1L, "field 1", GrassType.NATURAL_GRASS, true,
                new Location("zone b", "address 2"), owner);

        byte[] imageData = new byte[]{1, 2, 3};
        MultipartFile file = new MockMultipartFile("file", imageData);

        Image image = new Image(imageData, field);
        image.setId(1L);
        when(imageRepository.save(any())).thenReturn(image);

        imageService.saveImages(field, List.of(file));

        assertEquals(1, field.getImages().size());
        Image firstImage = field.getImages().getFirst();
        assertArrayEquals(imageData, firstImage.getData());
        assertEquals(1L, firstImage.getId());
        assertEquals(field, firstImage.getField());
    }

    @Test
    void saveImages_whenManyImages_saveImages() throws IOException {
        Field field = new Field(1L, "field 1", GrassType.NATURAL_GRASS, true,
                new Location("zone b", "address 2"), owner);

        byte[] imageData1 = new byte[]{1, 2, 3};
        byte[] imageData2 = new byte[]{4, 5, 6};

        MultipartFile file1 = new MockMultipartFile("file1", imageData1);
        MultipartFile file2 = new MockMultipartFile("file2", imageData2);

        Image image1 = new Image(imageData1, field);
        image1.setId(1L);

        Image image2 = new Image(imageData2, field);
        image2.setId(2L);

        when(imageRepository.save(any(Image.class)))
                .thenReturn(image1)
                .thenReturn(image2);

        imageService.saveImages(field, List.of(file1, file2));

        assertEquals(2, field.getImages().size());

        Image firstImage = field.getImages().getFirst();
        assertArrayEquals(imageData1, firstImage.getData());
        assertEquals(1L, firstImage.getId());
        assertEquals(field, firstImage.getField());

        Image secondImage = field.getImages().get(1);
        assertArrayEquals(imageData2, secondImage.getData());
        assertEquals(2L, secondImage.getId());
        assertEquals(field, secondImage.getField());
    }

    @Test
    void getImageData_whenImageExists_returnsImageData() throws ItemNotFoundException {
        byte[] imageData = new byte[]{1, 2, 3};
        Image image = new Image(imageData, owner);
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
            imageService.deleteImage(1L)
        );

        assertEquals("Failed to find image with id '1'", exception.getMessage());
    }

    @Test
    void deleteImage_whenImageExists_deletesImage() throws ItemNotFoundException {
        Image image = new Image(new byte[]{1, 2, 3}, owner);
        image.setId(1L);

        when(imageRepository.findById(anyLong())).thenReturn(Optional.of(image));

        imageService.deleteImage(1L);
        verify(imageRepository).delete(image);
    }
}
