package ar.uba.fi.ingsoft1.football5.images;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ImageRepository extends JpaRepository<Image, Long> {
    @Query("SELECT i FROM AvatarImage WHERE i.user_id = :userId")
    Optional<AvatarImage> findByUserId(@Param("userId") Long userId);

    @Query("SELECT i FROM FieldImage WHERE i.field_id = :fieldId")
    Optional<FieldImage> findByFieldId(@Param("fieldId") Long fieldId);
}
