package ar.uba.fi.ingsoft1.football5.fields;

/**
 * Estadísticas de ocupación de la cancha, tanto en el pasado como en el futuro.
 */
public record FieldStatsDTO(
    // — Estadísticas PASADAS (desde hace una semana / un mes hasta hoy)

    /** % de horas reservadas en la última semana */
    double pastWeeklyPct,
    /** % de horas reservadas en el último mes */
    double pastMonthlyPct,
    /** Horas reservadas semana pasada */
    double pastReservedHoursWeek,
    /** Horas disponibles semana pasada */
    double pastAvailableHoursWeek,
    /** Horas reservadas mes pasado */
    double pastReservedHoursMonth,
    /** Horas disponibles mes pasado */
    double pastAvailableHoursMonth,
    /** Partidos cancelados en la última semana */
    long pastCancelledWeek,
    /** Partidos cancelados en el último mes */
    long pastCancelledMonth,

    // — Estadísticas FUTURAS (desde hoy hasta dentro de una semana / un mes)

    /** % de horas ya reservadas en la próxima semana */
    double futureWeeklyPct,
    /** % de horas ya reservadas en el próximo mes */
    double futureMonthlyPct,
    /** Horas reservadas próxima semana */
    double futureReservedHoursWeek,
    /** Horas disponibles próxima semana */
    double futureAvailableHoursWeek,
    /** Horas reservadas próximo mes */
    double futureReservedHoursMonth,
    /** Horas disponibles próximo mes */
    double futureAvailableHoursMonth,
    /** Partidos cancelados en la próxima semana */
    long futureCancelledWeek,
    /** Partidos cancelados en el próximo mes */
    long futureCancelledMonth
) {
    public FieldStatsDTO {
        // redondeo a 1 decimal para todas las métricas de porcentaje/horas
        pastWeeklyPct            = Math.round(pastWeeklyPct            * 10) / 10.0;
        pastMonthlyPct           = Math.round(pastMonthlyPct           * 10) / 10.0;
        pastReservedHoursWeek    = Math.round(pastReservedHoursWeek    * 10) / 10.0;
        pastAvailableHoursWeek   = Math.round(pastAvailableHoursWeek   * 10) / 10.0;
        pastReservedHoursMonth   = Math.round(pastReservedHoursMonth   * 10) / 10.0;
        pastAvailableHoursMonth  = Math.round(pastAvailableHoursMonth  * 10) / 10.0;

        futureWeeklyPct          = Math.round(futureWeeklyPct          * 10) / 10.0;
        futureMonthlyPct         = Math.round(futureMonthlyPct         * 10) / 10.0;
        futureReservedHoursWeek  = Math.round(futureReservedHoursWeek  * 10) / 10.0;
        futureAvailableHoursWeek = Math.round(futureAvailableHoursWeek * 10) / 10.0;
        futureReservedHoursMonth = Math.round(futureReservedHoursMonth * 10) / 10.0;
        futureAvailableHoursMonth= Math.round(futureAvailableHoursMonth* 10) / 10.0;
        // los conteos de partidos quedan como están (long), no redondeamos
    }
}
