package ar.uba.fi.ingsoft1.football5.images;

import ar.uba.fi.ingsoft1.football5.common.exception.ItemNotFoundException;
import ar.uba.fi.ingsoft1.football5.common.exception.UserNotFoundException;
import ar.uba.fi.ingsoft1.football5.config.security.JwtUserDetails;
import ar.uba.fi.ingsoft1.football5.fields.Field;
import ar.uba.fi.ingsoft1.football5.fields.FieldRepository;
import ar.uba.fi.ingsoft1.football5.user.User;
import ar.uba.fi.ingsoft1.football5.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FieldImageServiceTest {

    @Mock
    private FieldRepository fieldRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FieldImageService fieldImageService;

    @Mock
    private JwtUserDetails userDetails;

    @Mock
    private User user;

    @Mock
    private User anotherUser;

    @Mock
    private Image image;

    @Mock
    private Field field;

    @Test
    void validateFieldImageOwnership_whenUserNotFound_ThrowsUserNotFoundException() {
        when(userDetails.username()).thenReturn("test-user");
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            fieldImageService.validateFieldImageOwnership(image, userDetails);
        });

        assertEquals("Failed to find user with username test-user", exception.getMessage());
    }

    @Test
    void validateFieldImageOwnership_whenFieldNotFound_ThrowsItemNotFoundException() {
        when(userDetails.username()).thenReturn("test-user");

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        when(image.getField()).thenReturn(field);
        when(field.getId()).thenReturn(1L);

        when(fieldRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ItemNotFoundException.class, () -> {
            fieldImageService.validateFieldImageOwnership(image, userDetails);
        });

        assertEquals("Failed to find field with id '1'", exception.getMessage());
    }

    @Test
    void validateFieldImageOwnership_whenUserIsNotOwner_ThrowsIllegalArgumentException() {
        when(userDetails.username()).thenReturn("test-user");

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(user.getUsername()).thenReturn("test-user");

        when(image.getField()).thenReturn(field);
        when(image.getField().getId()).thenReturn(1L);

        when(fieldRepository.findById(1L)).thenReturn(Optional.of(field));
        when(field.getOwner()).thenReturn(anotherUser);
        when(anotherUser.getUsername()).thenReturn("another-user");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            fieldImageService.validateFieldImageOwnership(image, userDetails);
        });

        assertEquals("You do not own the field associated with this image.", exception.getMessage());
    }

    @Test
    void validateFieldImageOwnership_whenUserOwnsFieldImage_DoesNotThrowException() {
        when(userDetails.username()).thenReturn("test-user");

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(user.getUsername()).thenReturn("test-user");

        when(image.getField()).thenReturn(field);
        when(image.getField().getId()).thenReturn(1L);

        when(fieldRepository.findById(1L)).thenReturn(Optional.of(field));
        when(field.getOwner()).thenReturn(user);

        assertDoesNotThrow(() -> fieldImageService.validateFieldImageOwnership(image, userDetails));
    }
}
