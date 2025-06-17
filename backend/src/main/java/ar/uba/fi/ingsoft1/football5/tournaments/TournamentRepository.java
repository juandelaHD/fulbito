package ar.uba.fi.ingsoft1.football5.tournaments;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    boolean existsByName(String name);
}