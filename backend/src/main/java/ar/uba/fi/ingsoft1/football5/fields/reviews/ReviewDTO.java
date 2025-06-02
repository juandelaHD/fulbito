package ar.uba.fi.ingsoft1.football5.fields.reviews;

import java.time.LocalDateTime;

public record ReviewDTO(
        Integer rating,
        String comment,
        Long fieldId,
        Long userId,
        LocalDateTime createdAt
) {
    public ReviewDTO(Review review) {
        this(
                review.getRating(),
                review.getComment(),
                review.getField().getId(),
                review.getUser().getId(),
                review.getCreatedAt()
        );
    }
}
