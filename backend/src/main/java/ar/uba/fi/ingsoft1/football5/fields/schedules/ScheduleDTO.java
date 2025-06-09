package ar.uba.fi.ingsoft1.football5.fields.schedules;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;

public record ScheduleDTO(
        @Schema(type = "integer", example = "1")
        Long id,
        @Schema(type = "string", format = "date", example = "2025-06-08")
        LocalDate date,
        @Schema(type = "string", format = "time", example = "09:00")
        LocalTime startTime,
        @Schema(type = "string", format = "time", example = "10:00")
        LocalTime endTime,
        @Schema(type = "string", example = "AVAILABLE")
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