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
    double pastReservedHours,
    /** 
     * Total de horas disponibles (slots creados) en la última semana. 
     */
    double pastAvailableHours,

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
    double futureReservedHours,
    /** 
     * Total de horas disponibles (slots creados) en la próxima semana. 
     */
    double futureAvailableHours
) {
    public FieldStatsDTO {
        // redondeo a 1 decimal para todas las métricas
        pastWeeklyPct        = Math.round(pastWeeklyPct        * 10) / 10.0;
        pastMonthlyPct       = Math.round(pastMonthlyPct       * 10) / 10.0;
        pastReservedHours    = Math.round(pastReservedHours    * 10) / 10.0;
        pastAvailableHours   = Math.round(pastAvailableHours   * 10) / 10.0;
        futureWeeklyPct      = Math.round(futureWeeklyPct      * 10) / 10.0;
        futureMonthlyPct     = Math.round(futureMonthlyPct     * 10) / 10.0;
        futureReservedHours  = Math.round(futureReservedHours  * 10) / 10.0;
        futureAvailableHours = Math.round(futureAvailableHours * 10) / 10.0;
    }
}
