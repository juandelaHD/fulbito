package ar.uba.fi.ingsoft1.football5.fields;

/**
 * Estadísticas de ocupación de la cancha, tanto en el pasado como en el futuro.
 */
public record FieldStatsDTO(
    // — Estadísticas PASADAS (desde hace una semana / un mes hasta hoy)

    /** 
     * Porcentaje de horas reservadas de la cancha en la última semana 
     * (horas reservadas ÷ horas disponibles * 100). 
     */
    double pastWeeklyPct,
    /** 
     * Porcentaje de horas reservadas de la cancha en el último mes 
     * (horas reservadas ÷ horas disponibles * 100). 
     */
    double pastMonthlyPct,
    /** 
     * Total de horas efectivamente reservadas en la última semana. 
     */
    double pastReservedHoursWeek,
    /** 
     * Total de horas disponibles (slots creados) en la última semana. 
     */
    double pastAvailableHoursWeek,
    /** 
     * Total de horas efectivamente reservadas en el último mes. 
     */
    double pastReservedHoursMonth,
    /** 
     * Total de horas disponibles (slots creados) en el último mes. 
     */
    double pastAvailableHoursMonth,

    // — Estadísticas FUTURAS (desde hoy hasta dentro de una semana / un mes)

    /** 
     * Porcentaje de horas ya reservadas de la cancha en la próxima semana 
     * (horas reservadas ÷ horas disponibles * 100). 
     */
    double futureWeeklyPct,
    /** 
     * Porcentaje de horas ya reservadas de la cancha en el próximo mes 
     * (horas reservadas ÷ horas disponibles * 100). 
     */
    double futureMonthlyPct,
    /** 
     * Total de horas reservadas (slots ocupados) en la próxima semana. 
     */
    double futureReservedHoursWeek,
    /** 
     * Total de horas disponibles (slots creados) en la próxima semana. 
     */
    double futureAvailableHoursWeek,
    /** 
     * Total de horas reservadas (slots ocupados) en el próximo mes. 
     */
    double futureReservedHoursMonth,
    /** 
     * Total de horas disponibles (slots creados) en el próximo mes. 
     */
    double futureAvailableHoursMonth
) {
    public FieldStatsDTO {
        // redondeo a 1 decimal para todas las métricas
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
    }
}
