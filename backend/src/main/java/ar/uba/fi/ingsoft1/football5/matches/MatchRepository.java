package ar.uba.fi.ingsoft1.football5.matches;

import ar.uba.fi.ingsoft1.football5.fields.Field;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByFieldAndStatusAndStartTimeAfter(Field field, MatchStatus status, LocalDateTime now);


    @Query("""
    SELECT m FROM Match m
    WHERE m.field.id = :fieldId
    AND m.date = :date
    AND (
        (:startTime < m.endTime AND :endTime > m.startTime)
    )
    """)
    List<Match> findConflictingMatches(
            @Param("fieldId") Long fieldId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    @Query("""
    SELECT m FROM Match m
    WHERE m.type = 'OPEN'
    AND m.status = 'SCHEDULED'
    AND m.startTime > :now
    AND size(m.players) < m.maxPlayers
    """)
    List<Match> findAvailableOpenMatches(@Param("now") LocalDateTime now);
}
