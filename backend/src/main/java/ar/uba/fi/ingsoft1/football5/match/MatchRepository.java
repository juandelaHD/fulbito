package ar.uba.fi.ingsoft1.football5.match;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findBycloseMatchFalse();
}
