package ar.uba.fi.ingsoft1.football5.fields;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FieldRepository extends JpaRepository<Field, Long> {
    Optional<Field> findByName(String name);
    Optional<Field> findByLocationZoneAndLocationAddress(String zone, String address);
}
