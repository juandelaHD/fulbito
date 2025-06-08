package ar.uba.fi.ingsoft1.football5.fields.schedules;

import ar.uba.fi.ingsoft1.football5.fields.Field;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    Page<Schedule> findByField(Field field, Pageable pageable);
}
