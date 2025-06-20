package ar.uba.fi.ingsoft1.football5.fields;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FieldRepository extends JpaRepository<Field, Long>, JpaSpecificationExecutor<Field> {
    Optional<Field> findByName(String name);
    Optional<Field> findByLocationZoneAndLocationAddress(String zone, String address);
    Page<Field> findByOwnerId(Long ownerId, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"matches"})
    Page<Field> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"matches"})
    Page<Field> findAll(Specification<Field> spec, Pageable pageable);

    @Query("SELECT f FROM Field f LEFT JOIN FETCH f.images WHERE f.id = :id")
    Optional<Field> findByIdWithImages(@Param("id") Long id);
}
