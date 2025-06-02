package ar.uba.fi.ingsoft1.football5.fields.reviews;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByFieldId(Long fieldId, Pageable pageable);
}
