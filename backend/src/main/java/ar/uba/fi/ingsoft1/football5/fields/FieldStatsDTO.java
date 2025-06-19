package ar.uba.fi.ingsoft1.football5.fields;

public record FieldStatsDTO(
    double weeklyPercentage,
    double monthlyPercentage,
    double reservedHours,
    double availableHours
) {
    public FieldStatsDTO {
        // este es el constructor canónico; puedes validar o transformar:
        weeklyPercentage  = Math.round(weeklyPercentage  * 10) / 10.0;
        monthlyPercentage = Math.round(monthlyPercentage * 10) / 10.0;
        reservedHours     = Math.round(reservedHours     * 10) / 10.0;
        availableHours    = Math.round(availableHours    * 10) / 10.0;
    }
}
