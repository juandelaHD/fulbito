package ar.uba.fi.ingsoft1.football5.fields.schedules;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record ScheduleCreateDTO(
        @NotNull(message = "startDate cannot be null")
        LocalDate startDate,

        @NotNull(message = "endDate cannot be null")
        LocalDate endDate,

        @NotNull(message = "openingTime cannot be null")
        LocalTime openingTime,

        @NotNull(message = "closingTime cannot be null")
        LocalTime closingTime,

        @NotNull(message = "slotDurationMinutes cannot be null")
        @Min(value = 30, message = "slotDurationMinutes must be at least 30 minutes")
        @Max(value = 120, message = "slotDurationMinutes must be at most 120 minutes")
        Integer slotDurationMinutes,

        @NotNull(message = "breakDurationMinutes cannot be null")
        @Min(value = 0, message = "breakDurationMinutes cannot be negative")
        Integer breakDurationMinutes,

        @NotEmpty(message = "daysOfWeek cannot be empty")
        List<DayOfWeek> daysOfWeek
) {
    public ScheduleCreateDTO {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
        if (startDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Start date cannot be in the past");
        }
        if (openingTime.isAfter(closingTime)) {
            throw new IllegalArgumentException("OpeningTime must be before ClosingTime");
        }
    }
}
