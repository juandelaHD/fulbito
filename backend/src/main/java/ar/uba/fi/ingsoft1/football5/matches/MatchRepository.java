package ar.uba.fi.ingsoft1.football5.matches;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ar.uba.fi.ingsoft1.football5.fields.Field;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByFieldAndStartTimeAfter(Field field, LocalDateTime now);

    // MatchRepository.java
    @Query("SELECT m FROM Match m WHERE m.field.id = :fieldId AND m.date = :date " +
            "AND m.startTime < :endTime AND m.endTime > :startTime " +
            "AND m.status NOT IN (:excludedStatuses)")
    List<Match> findConflictingMatches(Long fieldId, LocalDate date, LocalDateTime startTime, LocalDateTime endTime, List<MatchStatus> excludedStatuses);

    @Query("""
    SELECT m FROM Match m
    WHERE m.type = ar.uba.fi.ingsoft1.football5.matches.MatchType.OPEN
      AND m.status IN :statuses
      AND m.startTime > :now
      AND SIZE(m.players) < m.maxPlayers
    """)
    List<Match> findByTypeAndStatusInAndStartTimeAfterAndPlayers_SizeLessThan(
            @Param("statuses") List<MatchStatus> statuses,
            @Param("now") LocalDateTime now
    );


    List<Match> findByFieldIdAndStartTimeBetween(
        Long fieldId,
        LocalDateTime from,
        LocalDateTime to
    );

    Page<Match> findByFieldId(Long fieldId, Pageable pageable);
    Page<Match> findByFieldIdAndDate(Long fieldId, LocalDate date, Pageable pageable);
    Page<Match> findByFieldIdAndStatus(Long fieldId, MatchStatus status, Pageable pageable);
    Page<Match> findByFieldIdAndStatusAndDate(Long fieldId, MatchStatus status, LocalDate date, Pageable pageable);
}
