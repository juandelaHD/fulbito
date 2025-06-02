package ar.uba.fi.ingsoft1.football5.fields.reviews;

import ar.uba.fi.ingsoft1.football5.common.exception.ItemNotFoundException;
import ar.uba.fi.ingsoft1.football5.common.exception.UserNotFoundException;
import ar.uba.fi.ingsoft1.football5.config.security.JwtUserDetails;
import ar.uba.fi.ingsoft1.football5.fields.Field;
import ar.uba.fi.ingsoft1.football5.fields.FieldService;
import ar.uba.fi.ingsoft1.football5.fields.GrassType;
import ar.uba.fi.ingsoft1.football5.fields.Location;
import ar.uba.fi.ingsoft1.football5.user.User;
import ar.uba.fi.ingsoft1.football5.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private FieldService fieldService;

    @Mock
    private UserService userService;

    @Mock
    private JwtUserDetails userDetails;

    @Mock
    private User user;

    @Mock
    private Location location;

    @InjectMocks
    private ReviewService reviewServiceTest;

    @Test
    void getReviewsByFieldId_whenFieldNotFound_throwsItemNotFoundException() throws ItemNotFoundException {
        Long fieldId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        when(fieldService.loadFieldById(fieldId)).thenThrow(new ItemNotFoundException("field", fieldId));

        ItemNotFoundException exception = assertThrows(ItemNotFoundException.class, () ->
            reviewServiceTest.getReviewsByFieldId(fieldId, pageable)
        );
        assertEquals("Failed to find field with id '1'", exception.getMessage());
    }

    @Test
    void getReviewsByFieldId_whenFieldFound_returnsPageOfReviews() throws ItemNotFoundException {
        Long fieldId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        when(fieldService.loadFieldById(fieldId)).thenReturn(new Field(fieldId, "Test Field",
                GrassType.NATURAL_GRASS, true, location, user));
        when(reviewRepository.findByFieldId(fieldId, pageable)).thenReturn(Page.empty());

        Page<ReviewDTO> result = reviewServiceTest.getReviewsByFieldId(fieldId, pageable);
        assertNotNull(result);
    }

    @Test
    void createReview_whenFieldNotFound_throwsItemNotFoundException() throws ItemNotFoundException {
        Long fieldId = 1L;
        ReviewCreateDTO reviewCreateDTO = new ReviewCreateDTO(10, "Great field!");

        when(fieldService.loadFieldById(fieldId)).thenThrow(new ItemNotFoundException("field", fieldId));

        ItemNotFoundException exception = assertThrows(ItemNotFoundException.class, () ->
            reviewServiceTest.createReview(reviewCreateDTO, fieldId, userDetails)
        );
        assertEquals("Failed to find field with id '1'", exception.getMessage());
    }

    @Test
    void createReview_whenUserNotFound_throwsItemNotFoundException() throws UserNotFoundException, ItemNotFoundException {
        Long fieldId = 1L;
        String username = "testUser";
        ReviewCreateDTO reviewCreateDTO = new ReviewCreateDTO(10, "Great field!");
        Field field = new Field(fieldId, "Test Field", GrassType.NATURAL_GRASS, true,
                mock(Location.class), user);

        when(userDetails.username()).thenReturn(username);

        when(fieldService.loadFieldById(fieldId)).thenReturn(field);
        when(userService.loadUserByUsername(userDetails.username())).thenThrow(new UserNotFoundException("user", username));

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () ->
            reviewServiceTest.createReview(reviewCreateDTO, fieldId, userDetails)
        );
        assertEquals("Failed to find user with username testUser", exception.getMessage());
    }

    @Test
    void createReview_whenValidData_returnsReviewDTO() throws ItemNotFoundException {
        Long fieldId = 1L;
        ReviewCreateDTO reviewCreateDTO = new ReviewCreateDTO(10, "Great field!");
        Field field = new Field(fieldId, "Test Field", GrassType.NATURAL_GRASS, true,
                mock(Location.class), user);
        Review review = new Review(reviewCreateDTO.rating(), reviewCreateDTO.comment(), field, user);

        when(userDetails.username()).thenReturn("testUser");
        when(fieldService.loadFieldById(fieldId)).thenReturn(field);
        when(userService.loadUserByUsername(userDetails.username())).thenReturn(user);
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        ReviewDTO result = reviewServiceTest.createReview(reviewCreateDTO, fieldId, userDetails);

        assertNotNull(result);
        assertEquals(reviewCreateDTO.rating(), result.rating());
        assertEquals(reviewCreateDTO.comment(), result.comment());
        assertEquals(fieldId, result.fieldId());
        assertEquals(user.getId(), result.userId());
    }
}
