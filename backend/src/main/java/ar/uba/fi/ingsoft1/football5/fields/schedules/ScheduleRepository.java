package ar.uba.fi.ingsoft1.football5.fields.schedules;

import ar.uba.fi.ingsoft1.football5.fields.Field;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    Page<Schedule> findByField(Field field, Pageable pageable);
    List<Schedule> findByFieldAndDate(Field field, LocalDate date);
    Page<Schedule> findByFieldAndStatus(Field field, ScheduleStatus status, Pageable pageable);
}
