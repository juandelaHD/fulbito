package ar.uba.fi.ingsoft1.football5.matches;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByType(MatchType type);
    List<Match> findByStatus(MatchStatus status);
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
}
