package ar.uba.fi.ingsoft1.football5.fields.reviews;

import ar.uba.fi.ingsoft1.football5.common.exception.ItemNotFoundException;
import ar.uba.fi.ingsoft1.football5.common.exception.UserNotFoundException;
import ar.uba.fi.ingsoft1.football5.config.security.JwtUserDetails;
import ar.uba.fi.ingsoft1.football5.fields.Field;
import ar.uba.fi.ingsoft1.football5.fields.FieldService;
import ar.uba.fi.ingsoft1.football5.matches.Match;
import ar.uba.fi.ingsoft1.football5.matches.MatchStatus;
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

import java.time.LocalDateTime;
import java.util.Set;

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
    private Field field;

    @Mock
    private Match match;

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

        when(fieldService.loadFieldById(fieldId)).thenReturn(field);
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

        when(userDetails.username()).thenReturn(username);
        when(fieldService.loadFieldById(fieldId)).thenReturn(field);
        when(userService.loadUserByUsername(userDetails.username())).thenThrow(new UserNotFoundException("user", username));

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () ->
            reviewServiceTest.createReview(reviewCreateDTO, fieldId, userDetails)
        );
        assertEquals("Failed to find user with username testUser", exception.getMessage());
    }

    @Test
    void createReview_whenUserHasAlreadyReviewedField_throwsIllegalArgumentException() throws ItemNotFoundException {
        Long fieldId = 1L;
        ReviewCreateDTO reviewCreateDTO = new ReviewCreateDTO(10, "Great field!");

        when(userDetails.username()).thenReturn("testUser");
        when(fieldService.loadFieldById(fieldId)).thenReturn(field);
        when(userService.loadUserByUsername(userDetails.username())).thenReturn(user);
        when(reviewRepository.existsByFieldAndUser(field, user)).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            reviewServiceTest.createReview(reviewCreateDTO, fieldId, userDetails)
        );
        assertEquals("User has already reviewed this field.", exception.getMessage());
    }

    @Test
    void createField_whenUserHasNotPlayedInTheField_throwsIllegalArgumentException() throws ItemNotFoundException {
        Long fieldId = 1L;
        ReviewCreateDTO reviewCreateDTO = new ReviewCreateDTO(10, "Great field!");
        Field anotherField = mock(Field.class);

        when(match.getField()).thenReturn(anotherField);
        when(anotherField.getId()).thenReturn(2L);

        when(userDetails.username()).thenReturn("testUser");
        when(fieldService.loadFieldById(fieldId)).thenReturn(field);
        when(userService.loadUserByUsername(userDetails.username())).thenReturn(user);
        when(user.getJoinedMatches()).thenReturn(Set.of(match));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            reviewServiceTest.createReview(reviewCreateDTO, fieldId, userDetails)
        );
        assertEquals("User must have played a past scheduled match in this field to review it.", exception.getMessage());
    }

    @Test
    void createReview_whenUserCanReviewField_returnsReviewDTO() throws ItemNotFoundException {
        Long fieldId = 1L;
        ReviewCreateDTO reviewCreateDTO = new ReviewCreateDTO(10, "Great field!");
        Review review = new Review(reviewCreateDTO.rating(), reviewCreateDTO.comment(), field, user);

        when(match.getField()).thenReturn(field);
        when(match.getStatus()).thenReturn(MatchStatus.FINISHED);
        when(match.getStartTime()).thenReturn(LocalDateTime.now().minusDays(1)); // Simulate a past match
        when(field.getId()).thenReturn(1L);

        when(userDetails.username()).thenReturn("testUser");
        when(fieldService.loadFieldById(fieldId)).thenReturn(field);
        when(userService.loadUserByUsername(userDetails.username())).thenReturn(user);
        when(reviewRepository.save(any(Review.class))).thenReturn(review);
        when(user.getJoinedMatches()).thenReturn(Set.of(match));

        ReviewDTO result = reviewServiceTest.createReview(reviewCreateDTO, fieldId, userDetails);

        assertNotNull(result);
        assertEquals(reviewCreateDTO.rating(), result.rating());
        assertEquals(reviewCreateDTO.comment(), result.comment());
        assertEquals(fieldId, result.fieldId());
        assertEquals(user.getId(), result.userId());
    }
}
