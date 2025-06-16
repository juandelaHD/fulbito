package ar.uba.fi.ingsoft1.football5.fields.reviews;

import ar.uba.fi.ingsoft1.football5.common.exception.ItemNotFoundException;
import ar.uba.fi.ingsoft1.football5.config.security.JwtUserDetails;
import ar.uba.fi.ingsoft1.football5.fields.Field;
import ar.uba.fi.ingsoft1.football5.fields.FieldService;
import ar.uba.fi.ingsoft1.football5.matches.MatchStatus;
import ar.uba.fi.ingsoft1.football5.user.User;
import ar.uba.fi.ingsoft1.football5.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final FieldService fieldService;
    private final UserService userService;

    public ReviewService(ReviewRepository reviewRepository, FieldService fieldService,
                         UserService userService) {
        this.reviewRepository = reviewRepository;
        this.fieldService = fieldService;
        this.userService = userService;
    }

    public ReviewDTO createReview(ReviewCreateDTO reviewCreateDTO, Long fieldId, JwtUserDetails userDetails)
            throws ItemNotFoundException {
        Field field = fieldService.loadFieldById(fieldId);
        User user = userService.loadUserByUsername(userDetails.username());
        validateUserHasNotReviewedField(field, user);
        validateUserCanReviewField(field, user);

        Review review = new Review(
                reviewCreateDTO.rating(),
                reviewCreateDTO.comment(),
                field,
                user
        );

        reviewRepository.save(review);
        return new ReviewDTO(review);
    }

    private void validateUserCanReviewField(Field field, User user) {
        boolean hasPlayedPastScheduledMatch = user.getJoinedMatches().stream()
                .anyMatch(match -> match.getField().getId().equals(field.getId())
                        && match.getStatus() == MatchStatus.FINISHED
                        && match.getStartTime().isBefore(LocalDateTime.now()));

        if (!hasPlayedPastScheduledMatch) {
            throw new IllegalArgumentException("User must have played a past scheduled match in this field to review it.");
        }
    }

    private void validateUserHasNotReviewedField(Field field, User user) {
        if (reviewRepository.existsByFieldAndUser(field, user)) {
            throw new IllegalArgumentException("User has already reviewed this field.");
        }
    }

    public Page<ReviewDTO> getReviewsByFieldId(Long fieldId, Pageable pageable) throws ItemNotFoundException {
        fieldService.loadFieldById(fieldId);
        return reviewRepository.findByFieldId(fieldId, pageable)
                .map(ReviewDTO::new);
    }
}
