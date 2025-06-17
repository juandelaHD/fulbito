package ar.uba.fi.ingsoft1.football5.tournaments;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    boolean existsByName(String name);
    
    @Query("SELECT t FROM Tournament t JOIN FETCH t.organizer")
    List<Tournament> findAllWithOrganizer();
}