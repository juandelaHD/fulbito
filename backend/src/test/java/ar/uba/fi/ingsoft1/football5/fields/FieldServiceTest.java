package ar.uba.fi.ingsoft1.football5.fields;

import ar.uba.fi.ingsoft1.football5.common.exception.ItemNotFoundException;
import ar.uba.fi.ingsoft1.football5.config.security.JwtUserDetails;
import ar.uba.fi.ingsoft1.football5.images.ImageService;
import ar.uba.fi.ingsoft1.football5.matches.MatchRepository;
import ar.uba.fi.ingsoft1.football5.user.User;
import ar.uba.fi.ingsoft1.football5.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FieldServiceTest {

    @Mock
    private FieldRepository fieldRepository;

    @Mock
    private ImageService imageService;

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private UserService userService;

    @Mock
    private User owner;

    @Mock
    private JwtUserDetails userDetails;

    @InjectMocks
    private FieldService fieldService;

    @Test
    void createField_whenDuplicatedName_throwsIllegalArgumentException() {
        FieldCreateDTO fieldCreateDTO = new FieldCreateDTO("field 1", GrassType.NATURAL_GRASS, true,
                "zone a", "address 1");

        when(fieldRepository.findByName(fieldCreateDTO.name()))
                .thenReturn(Optional.of(new Field(1L, "field 1", GrassType.NATURAL_GRASS, true,
                        new Location("zone b", "address 2"), owner)));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            fieldService.createField(fieldCreateDTO, List.of(), userDetails)
        );

        assertEquals("Field with name 'field 1' already exists.", exception.getMessage());
    }

    @Test
    void createField_whenDuplicatedLocation_throwsIllegalArgumentException() {
        FieldCreateDTO fieldCreateDTO = new FieldCreateDTO("field 1", GrassType.NATURAL_GRASS, true,
                "zone a", "address 1");

        when(fieldRepository.findByName(fieldCreateDTO.name())).thenReturn(Optional.empty());
        when(fieldRepository.findByLocationZoneAndLocationAddress(fieldCreateDTO.zone(), fieldCreateDTO.address()))
                .thenReturn(Optional.of(new Field(1L, "field 2", GrassType.NATURAL_GRASS, true,
                        new Location("zone a", "address 1"), owner)));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            fieldService.createField(fieldCreateDTO, List.of(), userDetails)
        );

        assertEquals("Field with location 'zone a, address 1' already exists.", exception.getMessage());
    }

    @Test
    void createField_whenValidationsPassed_returnsCreatedField() throws IOException {
        FieldCreateDTO fieldCreateDTO = new FieldCreateDTO("field 1", GrassType.NATURAL_GRASS, true,
                "zone a", "address 1");
        Field savedField = new Field(1L, fieldCreateDTO.name(), fieldCreateDTO.grassType(),
                fieldCreateDTO.illuminated(), new Location(fieldCreateDTO.zone(), fieldCreateDTO.address()), owner);

        when(fieldRepository.findByName(fieldCreateDTO.name())).thenReturn(Optional.empty());
        when(fieldRepository.findByLocationZoneAndLocationAddress(fieldCreateDTO.zone(), fieldCreateDTO.address()))
                .thenReturn(Optional.empty());

        when(userService.loadUserByUsername(any())).thenReturn(owner);
        when(fieldRepository.save(any(Field.class))).thenReturn(savedField);

        FieldDTO fieldDTO = fieldService.createField(fieldCreateDTO, List.of(), userDetails);

        assertEquals(fieldCreateDTO.name(), fieldDTO.name());
        assertEquals(fieldCreateDTO.grassType(), fieldDTO.grassType());
        assertEquals(fieldCreateDTO.illuminated(), fieldDTO.illuminated());
        assertEquals(fieldCreateDTO.zone(), fieldDTO.location().zone());
        assertEquals(fieldCreateDTO.address(), fieldDTO.location().address());
        assertEquals(List.of(), fieldDTO.imageIds());
    }

    @Test
    void deleteField_whenFieldDoesNotExist_throwsItemNotFoundException() {
        when(fieldRepository.findById(1L)).thenReturn(Optional.empty());

        ItemNotFoundException exception = assertThrows(ItemNotFoundException.class, () ->
            fieldService.deleteField(1L, userDetails)
        );

        assertEquals("Failed to find field with id '1'", exception.getMessage());
    }

    @Test
    void deleteField_whenUserIsNotOwner_throwsAccessDeniedException() {
        Field field = new Field(1L, "field 1", GrassType.NATURAL_GRASS, true,
                new Location("zone a", "address 1"), owner);

        when(fieldRepository.findById(1L)).thenReturn(Optional.of(field));
        when(owner.getUsername()).thenReturn("ownerUser");
        when(userDetails.username()).thenReturn("otherUser");

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () ->
            fieldService.deleteField(1L, userDetails)
        );

        assertEquals("User does not have permission to delete field with id '1'.", exception.getMessage());
    }

    @Test
    void deleteField_whenUserIsOwner_deletesField() throws ItemNotFoundException {
        Field field = new Field(1L, "field 1", GrassType.NATURAL_GRASS, true,
                new Location("zone a", "address 1"), owner);

        when(fieldRepository.findById(1L)).thenReturn(Optional.of(field));
        when(owner.getUsername()).thenReturn("ownerUser");
        when(userDetails.username()).thenReturn("ownerUser");

        fieldService.deleteField(1L, userDetails);
        verify(fieldRepository).delete(field);
    }
}
