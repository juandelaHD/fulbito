package ar.uba.fi.ingsoft1.football5.tournaments;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface TournamentRepository extends JpaRepository<Tournament, Long>, JpaSpecificationExecutor<Tournament>{
    boolean existsByName(String name);
    
    @Query("SELECT t FROM Tournament t JOIN FETCH t.organizer")
    List<Tournament> findAllWithOrganizer();

    @Query("SELECT t FROM Tournament t JOIN FETCH t.organizer o WHERE o.username = :username")
    List<Tournament> findAllByOrganizerUsername(String username);

    @Query("SELECT t FROM Tournament t JOIN FETCH t.organizer WHERE t.status = ar.uba.fi.ingsoft1.football5.tournaments.TournamentStatus.OPEN_FOR_REGISTRATION")
    List<Tournament> findAllByStatusOpenForRegistration();

    @Query("""
            SELECT t FROM Tournament t JOIN FETCH t.organizer o WHERE o.username = :username 
            AND t.status = ar.uba.fi.ingsoft1.football5.tournaments.TournamentStatus.OPEN_FOR_REGISTRATION
            """)
    List<Tournament> findAllByOrganizerUsernameAndStatusOpenForRegistration(String username);
}