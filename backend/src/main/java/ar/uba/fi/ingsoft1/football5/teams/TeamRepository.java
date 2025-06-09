package ar.uba.fi.ingsoft1.football5.teams;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {
    boolean existsByName(String name);
    Optional<Team> findByName(String name);
    List<Team> findByCaptainId(Long captainId);
}
