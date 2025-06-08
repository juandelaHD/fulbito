package ar.uba.fi.ingsoft1.football5.fields.schedules;

public record ScheduleSlotDTO(
        Long id,
        String start,
        String end,
        boolean available
) {}