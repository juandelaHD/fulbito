package ar.uba.fi.ingsoft1.football5.fields.schedules;

import java.time.LocalDate;
import java.time.LocalTime;

public record ScheduleDTO(
        Long id,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        ScheduleStatus status
) {
    public ScheduleDTO(Schedule schedule) {
        this(
                schedule.getId(),
                schedule.getDate(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.getStatus()
        );
    }
}
