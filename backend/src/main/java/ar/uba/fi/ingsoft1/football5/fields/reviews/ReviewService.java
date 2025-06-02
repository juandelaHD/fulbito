package ar.uba.fi.ingsoft1.football5.fields.reviews;

import ar.uba.fi.ingsoft1.football5.common.exception.ItemNotFoundException;
import ar.uba.fi.ingsoft1.football5.config.security.JwtUserDetails;
import ar.uba.fi.ingsoft1.football5.fields.Field;
import ar.uba.fi.ingsoft1.football5.fields.FieldService;
import ar.uba.fi.ingsoft1.football5.user.User;
import ar.uba.fi.ingsoft1.football5.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
        //validateUserCanReviewField(field, user); - if has already played a match in the field, can review
        // validateUserHasNotReviewedField(field, user); - if has not reviewed the field yet

        Review review = new Review(
                reviewCreateDTO.rating(),
                reviewCreateDTO.comment(),
                field,
                user
        );

        reviewRepository.save(review);
        return new ReviewDTO(review);
    }

    public Page<ReviewDTO> getReviewsByFieldId(Long fieldId, Pageable pageable) throws ItemNotFoundException {
        fieldService.loadFieldById(fieldId);
        return reviewRepository.findByFieldId(fieldId, pageable)
                .map(ReviewDTO::new);
    }
}
