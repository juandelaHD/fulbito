package ar.uba.fi.ingsoft1.football5.fields.schedules;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@ValidScheduleCreate
public record ScheduleCreateDTO(
        @NotNull
        @Schema(type = "string", format = "date", example = "2025-06-08")
        LocalDate startDate,

        @NotNull
        @Schema(type = "string", format = "date", example = "2025-06-08")
        LocalDate endDate,

        @NotNull
        @Schema(type = "string", format = "time", example = "09:00")
        LocalTime openingTime,

        @NotNull
        @Schema(type = "string", format = "time", example = "18:00")
        LocalTime closingTime,

        @NotNull
        @Min(30) @Max(120)
        @Schema(type = "integer", example = "60")
        Integer slotDurationMinutes,

        @NotNull
        @Min(0)
        @Schema(type = "integer", example = "0")
        Integer breakDurationMinutes,

        @NotEmpty
        @Schema(example = "[\"MONDAY\",\"TUESDAY\", \"WEDNESDAY\", \"THURSDAY\", \"FRIDAY\"]")
        List<DayOfWeek> daysOfWeek
) {}
