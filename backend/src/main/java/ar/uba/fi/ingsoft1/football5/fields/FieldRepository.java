package ar.uba.fi.ingsoft1.football5.fields;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface FieldRepository extends JpaRepository<Field, Long>, JpaSpecificationExecutor<Field> {
    Optional<Field> findByName(String name);
    Optional<Field> findByLocationZoneAndLocationAddress(String zone, String address);

    @Override
    @EntityGraph(attributePaths = {"matches"})
    Page<Field> findAll(Specification<Field> spec, Pageable pageable);
}
